package com.interest.circle.service;

import com.interest.circle.exception.BusinessException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class FileService {

    @Value("${app.qiniu.access-key}")
    private String accessKey;

    @Value("${app.qiniu.secret-key}")
    private String secretKey;

    @Value("${app.qiniu.bucket}")
    private String bucket;

    @Value("${app.qiniu.domain}")
    private String domain;

    private static final List<String> ALLOWED_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "image/webp", "image/bmp"
    );

    private final AtomicReference<String> resolvedIp = new AtomicReference<>();

    public String uploadImage(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException(400, "文件不能为空");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new BusinessException(400, "仅支持 JPG/PNG/GIF/WEBP/BMP 格式的图片");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String key = "images/" + UUID.randomUUID() + extension;

        try {
            Auth auth = Auth.create(accessKey, secretKey);
            String uploadToken = auth.uploadToken(bucket);

            Configuration cfg = new Configuration(Region.autoRegion());
            UploadManager uploadManager = new UploadManager(cfg);

            Response response = uploadManager.put(file.getBytes(), key, uploadToken);
            if (!response.isOK()) {
                throw new BusinessException("上传七牛云失败: " + response.bodyString());
            }
        } catch (IOException e) {
            throw new BusinessException("文件读取失败: " + e.getMessage());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("上传七牛云失败: " + e.getMessage());
        }

        return "/api/file/" + key;
    }

    public byte[] downloadImage(String key) {
        try {
            String host = domain.replaceFirst("^https?://", "").replaceFirst("/$", "");
            String ip = resolveHost(host);

            try (Socket socket = new Socket(ip, 80)) {
                socket.setSoTimeout(15000);
                OutputStream out = socket.getOutputStream();
                String httpReq = "GET /" + key + " HTTP/1.1\r\n" +
                        "Host: " + host + "\r\n" +
                        "Connection: close\r\n\r\n";
                out.write(httpReq.getBytes());
                out.flush();

                InputStream in = socket.getInputStream();
                ByteArrayOutputStream headerBuf = new ByteArrayOutputStream();
                int prev = -1, curr;
                int crlfCount = 0;
                while ((curr = in.read()) != -1) {
                    headerBuf.write(curr);
                    if (curr == '\n' && prev == '\r') crlfCount++;
                    else if (curr != '\r') crlfCount = 0;
                    if (crlfCount == 2) break;
                    prev = curr;
                }

                String headers = headerBuf.toString();
                if (!headers.contains("200")) {
                    throw new BusinessException(404, "图片不存在");
                }

                ByteArrayOutputStream body = new ByteArrayOutputStream();
                byte[] buf = new byte[8192];
                int n;
                while ((n = in.read(buf)) != -1) {
                    body.write(buf, 0, n);
                }
                return body.toByteArray();
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("下载图片失败: " + e.getMessage());
        }
    }

    private String resolveHost(String host) {
        String cached = resolvedIp.get();
        if (cached != null) return cached;

        String[] dohUrls = {
            "https://dns.alidns.com/resolve?name=" + host + "&type=A",
            "https://dns.google/resolve?name=" + host + "&type=A"
        };

        for (String dohUrl : dohUrls) {
            try {
                Process process = new ProcessBuilder("curl", "-s", "--connect-timeout", "10", dohUrl)
                        .redirectErrorStream(true).start();
                String json;
                try (InputStream in = process.getInputStream()) {
                    json = new String(in.readAllBytes());
                }
                process.waitFor();
                int searchFrom = 0;
                while (true) {
                    int dataIdx = json.indexOf("\"data\":\"", searchFrom);
                    if (dataIdx == -1) break;
                    int start = dataIdx + 8;
                    int end = json.indexOf("\"", start);
                    if (end == -1) break;
                    String val = json.substring(start, end);
                    if (val.matches("\\d+\\.\\d+\\.\\d+\\.\\d+")) {
                        resolvedIp.set(val);
                        return val;
                    }
                    searchFrom = end + 1;
                }
            } catch (Exception ignored) {}
        }

        try {
            InetAddress addr = InetAddress.getByName(host);
            String ip = addr.getHostAddress();
            resolvedIp.set(ip);
            return ip;
        } catch (Exception e) {
            throw new BusinessException("无法解析七牛云域名: " + host);
        }
    }

    public String getContentType(String key) {
        if (key.endsWith(".png")) return "image/png";
        if (key.endsWith(".gif")) return "image/gif";
        if (key.endsWith(".webp")) return "image/webp";
        if (key.endsWith(".bmp")) return "image/bmp";
        return "image/jpeg";
    }
}

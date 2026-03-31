package com.interest.circle.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.zip.Deflater;

@Service
public class TencentIMService {

    @Value("${app.tencent-im.sdk-app-id}")
    private long sdkAppId;

    @Value("${app.tencent-im.secret-key}")
    private String secretKey;

    public String generateUserSig(String userId) {
        return genSig(userId, 86400 * 7);
    }

    private String genSig(String identifier, long expire) {
        long currTime = System.currentTimeMillis() / 1000;

        String contentToBeSigned = "TLS.identifier:" + identifier + "\n"
                + "TLS.sdkappid:" + sdkAppId + "\n"
                + "TLS.time:" + currTime + "\n"
                + "TLS.expire:" + expire + "\n";

        String sig;
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = mac.doFinal(contentToBeSigned.getBytes(StandardCharsets.UTF_8));
            sig = Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("生成 UserSig 失败", e);
        }

        String jsonStr = "{" +
                "\"TLS.ver\":\"2.0\"," +
                "\"TLS.identifier\":\"" + identifier + "\"," +
                "\"TLS.sdkappid\":" + sdkAppId + "," +
                "\"TLS.expire\":" + expire + "," +
                "\"TLS.time\":" + currTime + "," +
                "\"TLS.sig\":\"" + sig + "\"" +
                "}";

        Deflater compressor = new Deflater();
        compressor.setInput(jsonStr.getBytes(StandardCharsets.UTF_8));
        compressor.finish();
        byte[] compressedBytes = new byte[2048];
        int compressedLen = compressor.deflate(compressedBytes);
        compressor.end();

        byte[] base64Bytes = Base64.getEncoder().encode(
                Arrays.copyOfRange(compressedBytes, 0, compressedLen));

        for (int i = 0; i < base64Bytes.length; i++) {
            switch (base64Bytes[i]) {
                case '+': base64Bytes[i] = '*'; break;
                case '/': base64Bytes[i] = '-'; break;
                case '=': base64Bytes[i] = '_'; break;
            }
        }

        return new String(base64Bytes);
    }

    public String getUserImId(Long userId) {
        return "user_" + userId;
    }

    public long getSdkAppId() {
        return sdkAppId;
    }
}

package com.interest.circle.controller;

import com.interest.circle.dto.ApiResponse;
import com.interest.circle.service.FileService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/api/upload/image")
    public ApiResponse<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        String url = fileService.uploadImage(file);
        return ApiResponse.success(Map.of("url", url));
    }

    @GetMapping("/api/file/**")
    public ResponseEntity<byte[]> getFile(HttpServletRequest request) {
        String path = request.getRequestURI().substring("/api/file/".length());
        byte[] data = fileService.downloadImage(path);
        String contentType = fileService.getContentType(path);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .cacheControl(CacheControl.maxAge(7, TimeUnit.DAYS).cachePublic())
                .body(data);
    }
}

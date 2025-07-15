package com.geonpil.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@RestController
public class imageUploadController {
    @PostMapping("/upload-image")
    public Map<String, String> uploadImage(@RequestParam("image") MultipartFile file) throws IOException {
        // 저장 경로 설정 (예: static/upload/)
        String uploadDir = System.getProperty("user.dir") + "/upload";
        String originalFilename = file.getOriginalFilename();
        String fileName = UUID.randomUUID() + "_" + originalFilename;

        // 디렉토리 없으면 생성
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        // 실제 파일 저장
        File target = new File(dir, fileName);
        file.transferTo(target);

        // 클라이언트에 이미지 URL 반환
        String imageUrl = "/upload/" + fileName;
        return Map.of("url", imageUrl);
    }

}
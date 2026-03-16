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
        if (!dir.exists())
            dir.mkdirs();

        // 실제 파일 저장
        File target = new File(dir, fileName);
        file.transferTo(target);

        // 클라이언트에 이미지 URL 반환
        String imageUrl = "/upload/" + fileName;
        return Map.of("url", imageUrl);
    }

    @PostMapping("/upload-banner-image")
    public Map<String, String> uploadBannerImage(@RequestParam("image") MultipartFile file) throws IOException {
        // 배너만 별도 폴더에 저장
        String uploadDir = System.getProperty("user.dir") + "/upload/banners";
        String originalFilename = file.getOriginalFilename();
        String fileName = UUID.randomUUID() + "_" + originalFilename;

        File dir = new File(uploadDir);
        if (!dir.exists())
            dir.mkdirs();

        File target = new File(dir, fileName);
        file.transferTo(target);

        // 브라우저에서 쓸 URL (정적 리소스 매핑이 /upload/** 로 되어 있다고 가정)
        String imageUrl = "/upload/banners/" + fileName;
        return Map.of("url", imageUrl);
    }

}
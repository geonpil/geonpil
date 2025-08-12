package com.geonpil.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.Set;
import java.util.Optional;

@Service
public class FileStorageService {

    private final String rootUploadDir;

    public FileStorageService() {
        // Root directory where all files will be stored.
        this.rootUploadDir = System.getProperty("user.dir") + "/upload";
    }

    private static final long MAX_SIZE = 10 * 1024 * 1024; // 10MB

    private static final Set<String> ALLOWED_EXT = Set.of("jpg", "jpeg", "png", "gif", "pdf", "hwp", "hwpx", "doc", "docx");

    private static final Set<String> ALLOWED_MIME = Set.of(
            "image/jpeg",
            "image/png",
            "image/gif",
            "application/pdf",
            "application/x-hwp",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    // Some browsers/clients report HWPX as vendor-specific, owpml, or even zip/octet-stream
    private static final Set<String> ALLOWED_HWpx_MIME = Set.of(
            "application/vnd.hancom.hwpx",
            "application/owpml",
            "application/x-hwpml",
            "application/zip",
            "application/x-zip-compressed",
            "application/octet-stream"
    );

    /**
     * 파일을 지정한 하위 디렉터리에 저장하고, 실제 저장된 파일명을 반환합니다.
     *
     * @param file   업로드 파일
     * @param subDir board/123 와 같이 rootUploadDir 하위의 경로
     * @return 저장된 파일명 (UUID_원본명.ext)
     */
    public String store(MultipartFile file, String subDir) throws IOException {
        // 0) 기본 검증
        validateFile(file);

        // 1) 디렉터리 준비
        String dirPath = rootUploadDir + File.separator + subDir;
        File dir = new File(dirPath);
        if (!dir.exists()) dir.mkdirs();

        // 2) 파일명 생성
        String originalFilename = file.getOriginalFilename();
        String storedName = UUID.randomUUID() + "_" + originalFilename;

        // 3) 저장
        File target = new File(dir, storedName);
        file.transferTo(target);

        return storedName;
    }

    private void validateFile(MultipartFile file) {
        // 크기 제한
        if (file.getSize() > MAX_SIZE) {
            throw new IllegalStateException("파일이 너무 큽니다. 최대 10MB까지 허용됩니다.");
        }

        // 확장자 화이트리스트
        String origin = file.getOriginalFilename();
        String ext = Optional.ofNullable(origin)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(origin.lastIndexOf('.') + 1).toLowerCase())
                .orElse("");
        if (!ALLOWED_EXT.contains(ext)) {
            throw new IllegalStateException("허용되지 않는 파일 확장자입니다.");
        }

        // MIME 타입 화이트리스트 (간단 검사)
        String mime = Optional.ofNullable(file.getContentType()).orElse("").toLowerCase();

        // Special-case for HWPX: different agents often send varying content-types
        if ("hwpx".equals(ext)) {
            if (!ALLOWED_HWpx_MIME.contains(mime)) {
                throw new IllegalStateException("허용되지 않는 HWPX 파일 형식입니다.");
            }
            return;
        }

        if (!ALLOWED_MIME.contains(mime)) {
            throw new IllegalStateException("허용되지 않는 파일 형식입니다.");
        }
    }

    /**
     * 실제 파일을 삭제합니다 (예: 게시글 수정/삭제 시).
     */
    public void delete(String subDir, String storedName) {
        String path = rootUploadDir + File.separator + subDir + File.separator + storedName;
        File file = new File(path);
        if (file.exists()) file.delete();
    }

    /**
     * /upload/** 형태의 URL 경로를 생성합니다.
     */
    public String buildFileUrl(String subDir, String storedName) {
        return "/upload/" + subDir + "/" + storedName;
    }
} 
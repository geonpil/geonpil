package com.geonpil.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class FileStorageService {

    private final String rootUploadDir;

    public FileStorageService() {
        // Root directory where all files will be stored.
        this.rootUploadDir = System.getProperty("user.dir") + "/upload";
    }

    /**
     * 파일을 지정한 하위 디렉터리에 저장하고, 실제 저장된 파일명을 반환합니다.
     *
     * @param file   업로드 파일
     * @param subDir board/123 와 같이 rootUploadDir 하위의 경로
     * @return 저장된 파일명 (UUID_원본명.ext)
     */
    public String store(MultipartFile file, String subDir) throws IOException {
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
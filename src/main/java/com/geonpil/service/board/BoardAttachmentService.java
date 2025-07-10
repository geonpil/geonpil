package com.geonpil.service.board;

import com.geonpil.domain.book.BoardAttachment;
import com.geonpil.mapper.board.BoardAttachmentMapper;
import com.geonpil.mapper.board.BoardMapper;
import com.geonpil.service.FileStorageService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class BoardAttachmentService {

    private final BoardAttachmentMapper attachmentMapper;
    private final FileStorageService fileStorageService;
    private final BoardMapper boardMapper;

    public BoardAttachmentService(BoardAttachmentMapper attachmentMapper,
                                  FileStorageService fileStorageService,
                                  BoardMapper boardMapper) {
        this.attachmentMapper = attachmentMapper;
        this.fileStorageService = fileStorageService;
        this.boardMapper = boardMapper;
    }

    /**
     * 게시글 작성/수정 시 첨부파일 저장.
     */
    @Transactional
    public void saveFiles(Long postId, List<MultipartFile> files) throws IOException {
        if (files == null || files.isEmpty()) return;

        String subDir = "board/" + postId; // upload/board/{postId}/
        List<BoardAttachment> entities = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;
            String storedName = fileStorageService.store(file, subDir);
            String urlPath = fileStorageService.buildFileUrl(subDir, storedName);

            BoardAttachment entity = BoardAttachment.builder()
                    .postId(postId)
                    .originalName(file.getOriginalFilename())
                    .storedName(storedName)
                    .filePath(urlPath)
                    .fileSize(file.getSize())
                    .build();
            attachmentMapper.insert(entity);
            entities.add(entity);
        }
    }

    /**
     * 첨부파일 목록 조회
     */
    public List<BoardAttachment> getFiles(Long postId) {
        return attachmentMapper.findByPostId(postId);
    }

    /**
     * 첨부파일 삭제 (권한 체크 포함)
     */
    @Transactional
    public void deleteFile(Long attachmentId, Long currentUserId) {
        // 첨부 가져오기
        List<BoardAttachment> list = attachmentMapper.findByPostId(null);
        // For simplicity we'll load a single attachment
        BoardAttachment target = list.stream().filter(a -> a.getAttachmentId().equals(attachmentId)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("첨부파일을 찾을 수 없습니다."));

        // 게시글 소유자인지 확인
        Long postId = target.getPostId();
        var post = boardMapper.findById(postId);
        if (post == null || !post.getUserId().equals(currentUserId)) {
            throw new AccessDeniedException("삭제 권한이 없습니다.");
        }

        // 파일 삭제 & DB soft delete
        String subDir = "board/" + postId;
        fileStorageService.delete(subDir, target.getStoredName());
        attachmentMapper.softDeleteById(attachmentId);
    }
} 
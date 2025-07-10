package com.geonpil.domain.book;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardAttachment {
    private Long attachmentId;  // PK
    private Long postId;        // FK to board.post_id
    private String originalName; // original file name provided by user
    private String storedName;   // actual stored file name (UUID prefixed)
    private String filePath;     // relative/absolute path where file is stored
    private Long fileSize;       // size in bytes
    private Timestamp createdAt; // upload timestamp
    private Boolean isDeleted;   // soft delete flag
} 
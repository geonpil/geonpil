package com.geonpil.domain;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class BoardDTO {
    private Long postId;
    private Long userId;
    private int boardCode;
    private Long categoryId;
    private String categoryName;
    private String nickname;
    private String title;
    private String content;
    private int viewCount;
    private int likeCount;
    private int commentCount; // ✅ 댓글 수
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
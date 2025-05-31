package com.geonpil.domain;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Review {
    private Long reviewId;
    private Long bookId;
    private Long userId;
    private String nickname;       // 사용자 닉네임 (조회 시용)
    private Double rating;        // 1~5점
    private String content;
    private Boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
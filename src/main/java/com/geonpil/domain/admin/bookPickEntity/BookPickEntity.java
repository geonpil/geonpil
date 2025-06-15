package com.geonpil.domain.admin.bookPickEntity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookPickEntity {

    private Long bookPickId;             // book_picks PK
    private Long bookId;         // books 테이블 FK

    private String reason;       // 추천 이유
    private Integer displayOrder;// 노출 순서
    private Boolean isDeleted;   // 노출 여부

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
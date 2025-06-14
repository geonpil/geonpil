package com.geonpil.domain.admin;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BookPick {

    private Long book_pick_id;             // book_picks PK
    private Long bookId;         // books 테이블 FK

    private String reason;       // 추천 이유
    private Integer displayOrder;// 노출 순서
    private Boolean isVisible;   // 노출 여부

    private String isbn;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
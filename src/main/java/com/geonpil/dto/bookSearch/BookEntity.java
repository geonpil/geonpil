package com.geonpil.dto.bookSearch;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
public class BookEntity {
    private Long bookId;
    private String isbn;
    private String isbn10;
    private String isbn13;
    private String title;
    private String authors;       // 문자열로 저장
    private String translators;   // 문자열로 저장
    private String publisher;
    private String thumbnail;
    private String contents;
    private OffsetDateTime datetime;
    private Integer price;
    private Integer salePrice;
    private String category;
    private String status;
    private OffsetDateTime createdAt;
}

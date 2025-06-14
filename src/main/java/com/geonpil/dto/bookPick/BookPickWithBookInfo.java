package com.geonpil.dto.bookPick;

import lombok.Data;


@Data
public class BookPickWithBookInfo {
    // book_picks 테이블 정보
    private Long bookPickId;
    private Long bookId;
    private String isbn10;
    private String isbn13;
    private int displayOrder;
    private boolean isVisible;

    // books 테이블 정보
    private String title;
    private String authors;
    private String publisher;
    private String thumbnail;
}

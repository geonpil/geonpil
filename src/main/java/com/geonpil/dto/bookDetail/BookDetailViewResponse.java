package com.geonpil.dto.bookDetail;

import com.geonpil.dto.bookSearch.BookEntity;
import com.geonpil.dto.bookSearch.Meta;
import lombok.Data;

import java.util.List;

@Data
public class BookDetailViewResponse {
    private Long bookId;
    private String title;
    private String authors;
    private String translators;
    private String publisher;
    private String contents;
    private String thumbnail;
    private String isbn;
    private Integer price;
    private Integer salePrice;
    private String category;
    private String status;
    private String url;
    private String processedAutors;
}

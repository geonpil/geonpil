package com.geonpil.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@Data
public class Book {
    private Long bookId;
    private String isbn;
    private String title;
    private List<String> authors; // JSON에서 List<String> → String으로 가공
    private List<String> translators;
    private String publisher;
    private String thumbnail;
    private String contents;
    private OffsetDateTime datetime;
    private Integer price;
    @JsonProperty("sale_price")
    private Integer salePrice;
    private String category;
    private String status;
    private String url;
    private OffsetDateTime createdAt;
}

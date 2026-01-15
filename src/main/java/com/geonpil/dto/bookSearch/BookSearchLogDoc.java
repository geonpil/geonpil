package com.geonpil.dto.bookSearch;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;



@Data
@AllArgsConstructor
public class BookSearchLogDoc {
    private final String keyword;
    private Long userId;      // 로그인 안 했으면 null 가능
    private String ip;        // optional
    private String userAgent; // optional
    private Instant searchedAt;

}
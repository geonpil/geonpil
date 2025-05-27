package com.geonpil.dto.response;

import com.geonpil.domain.Book;
import lombok.Data;
import java.util.List;

@Data
public class BookSearchResponse {
    private Book document;        // 하나일 때
    private List<Book> documents;  // 검색 결과
    private Meta meta;             // 검색 메타 정보
}

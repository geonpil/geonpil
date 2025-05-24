package com.geonpil.dto.response;

import com.geonpil.domain.Book;
import lombok.Data;
import java.util.List;

@Data
public class BookSearchResponse {
    private List<Book> documents;  // 검색 결과
    private Meta meta;             // 검색 메타 정보
}

package com.geonpil.dto.bookSearch;

import com.geonpil.domain.Book;
import lombok.Data;

import java.util.List;

@Data
public class BookSearchViewResponse {
    private List<BookEntity> books;
    private Meta meta;
}

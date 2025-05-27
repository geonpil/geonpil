package com.geonpil.mapper.book;

import com.geonpil.domain.Book;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BookMapper {
    Book findByIsbn(String isbn);
    void insertBook(Book book);
}
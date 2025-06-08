package com.geonpil.mapper.book;

import com.geonpil.dto.bookSearch.BookEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BookMapper {
    BookEntity findByIsbn10(String isbn);
    BookEntity findByIsbn13(String isbn);
    void insertBook(BookEntity book);
    BookEntity findById(Long bookId);

}

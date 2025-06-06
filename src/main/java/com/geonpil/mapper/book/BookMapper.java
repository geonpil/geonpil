package com.geonpil.mapper.book;

import com.geonpil.domain.Book;
import com.geonpil.domain.entity.BookEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface BookMapper {
    BookEntity findByIsbn10(String isbn);
    BookEntity findByIsbn13(String isbn);
    void insertBook(BookEntity book);
    BookEntity findById(Long bookId);

}

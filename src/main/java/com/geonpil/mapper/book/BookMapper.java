package com.geonpil.mapper.book;

import com.geonpil.domain.Book;
import com.geonpil.domain.entity.BookEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BookMapper {
    BookEntity findByIsbn(String isbn);
    void insertBook(BookEntity book);
    BookEntity findById(Long bookId);

}

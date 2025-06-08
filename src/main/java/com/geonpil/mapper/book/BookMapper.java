package com.geonpil.mapper.book;

import com.geonpil.dto.bookDetail.BookEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BookMapper {
    BookEntity findByIsbn10(String isbn);
    BookEntity findByIsbn13(String isbn);
    void insertBook(BookEntity book);
    BookEntity findById(Long bookId);
    List<BookEntity> findLastestReviewedBook();

}

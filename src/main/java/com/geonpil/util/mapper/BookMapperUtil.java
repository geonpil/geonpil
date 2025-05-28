package com.geonpil.util.mapper;

import com.geonpil.domain.Book;
import com.geonpil.domain.entity.BookEntity;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

public class BookMapperUtil {


    public static BookEntity toEntity(Book dto) {
        BookEntity entity = new BookEntity();
        entity.setIsbn(dto.getIsbn());
        entity.setTitle(dto.getTitle());
        entity.setAuthors(String.join(", ", dto.getAuthors()));
        entity.setTranslators(String.join(", ", dto.getTranslators()));
        entity.setPublisher(dto.getPublisher());
        entity.setThumbnail(dto.getThumbnail());
        entity.setContents(dto.getContents());
        entity.setDatetime(dto.getDatetime());
        entity.setPrice(dto.getPrice());
        entity.setSalePrice(dto.getSalePrice());
        entity.setCategory(dto.getCategory());
        entity.setStatus("ACTIVE"); // 기본 상태
        entity.setCreatedAt(OffsetDateTime.now());

        return entity;
    }


    public static Book toDomain(BookEntity entity) {
        if (entity == null) return null;

        Book book = new Book();
        book.setBookId(entity.getBookId());
        book.setIsbn(entity.getIsbn());
        book.setTitle(entity.getTitle());

        // authors: 문자열 → List<String>
        List<String> authors = entity.getAuthors() != null ?
                Arrays.asList(entity.getAuthors().split(",\\s*")) : null;
        book.setAuthors(authors);

        book.setPublisher(entity.getPublisher());
        book.setThumbnail(entity.getThumbnail());
        book.setContents(entity.getContents());
        book.setDatetime(entity.getDatetime());
        book.setPrice(entity.getPrice());
        book.setSalePrice(entity.getSalePrice());
        book.setCategory(entity.getCategory());
        book.setStatus(entity.getStatus());
        book.setCreatedAt(entity.getCreatedAt());

        return book;
    }
}

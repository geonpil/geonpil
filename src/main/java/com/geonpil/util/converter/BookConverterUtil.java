package com.geonpil.util.converter;

import com.geonpil.domain.Book;
import com.geonpil.dto.bookDetail.BookDetailViewResponse;
import com.geonpil.dto.bookSearch.BookEntity;
import com.geonpil.dto.bookSearch.BookSearchResponse;
import com.geonpil.dto.bookSearch.BookSearchViewResponse;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BookConverterUtil {


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
        book.setAuthors(parseCommaSeparated(entity.getAuthors()));
        book.setTranslators(parseCommaSeparated(entity.getTranslators()));
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



    public static BookDetailViewResponse toDetailView(Book book) {
        BookDetailViewResponse dto = new BookDetailViewResponse();
        dto.setBookId(book.getBookId());
        dto.setTitle(book.getTitle());
        dto.setAuthors(book.getAuthorsString());
        dto.setTranslators(book.getTranslatorsString());
        dto.setPublisher(book.getPublisher());
        dto.setContents(book.getContents());
        dto.setThumbnail(book.getThumbnail());
        dto.setIsbn(book.getIsbn());
        dto.setPrice(book.getPrice());
        dto.setSalePrice(book.getSalePrice());
        dto.setCategory(book.getCategory());
        dto.setStatus(book.getStatus());
        dto.setUrl(book.getUrl());
        dto.setProcessedAutors(book.getProcessedAutors());
        return dto;
    }


/*    public static BookSearchViewResponse toSearchDetailView(BookSearchResponse bookSearchResponse) {
        BookSearchViewResponse dto = new BookSearchViewResponse();
        dto.setMeta(bookSearchResponse.getMeta());
        dto.setBooks(bookSearchResponse.getDocuments());
        return dto;
    }*/


    public static List<String> parseCommaSeparated(String str) {
        if (str == null || str.isBlank()) return new ArrayList<>();
        return Arrays.stream(str.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

}

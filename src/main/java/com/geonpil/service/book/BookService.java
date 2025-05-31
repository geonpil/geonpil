package com.geonpil.service.book;

import com.geonpil.domain.Book;
import com.geonpil.domain.entity.BookEntity;
import com.geonpil.external.ExternalBookApiClient;
import com.geonpil.mapper.book.BookMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.geonpil.util.mapper.BookMapperUtil.toDomain;
import static com.geonpil.util.mapper.BookMapperUtil.toEntity;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookMapper bookMapper;
    private final ExternalBookApiClient externalBookApiClient;


    public Book getBookById(Long bookId) {
        return toDomain(bookMapper.findById(bookId));
    }

    public Book getOrFetchBookByIsbn(String isbn) {
        BookEntity book = bookMapper.findByIsbn(isbn);
        if(book != null) return toDomain(book);


        Book fetched = externalBookApiClient.fetchBookByIsbn(isbn);
        BookEntity fetchedBookEntity = toEntity(fetched);
        bookMapper.insertBook(fetchedBookEntity);


        System.out.println("Generated ID: " + fetched.getBookId());


        return toDomain(fetchedBookEntity);
    }

}

package com.geonpil.service.book;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.geonpil.domain.Book;
import com.geonpil.dto.response.BookSearchResponse;
import com.geonpil.dto.response.Meta;
import com.geonpil.external.ExternalBookApiClient;
import com.geonpil.mapper.book.BookMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookMapper bookMapper;
    private final ExternalBookApiClient externalBookApiClient;

    public Book getOrFetchBookByIsbn(String isbn) {
        Book book = bookMapper.findByIsbn(isbn);
        if (book != null) {
            return book;
        }

        Book fetched = externalBookApiClient.fetchBookByIsbn(isbn);
        bookMapper.insertBook(fetched);
        return fetched;
    }

}

package com.geonpil.service.book;

import com.geonpil.domain.Book;
import com.geonpil.dto.bookDetail.BookDetailViewResponse;
import com.geonpil.dto.bookSearch.BookEntity;
import com.geonpil.external.ExternalBookApiClient;
import com.geonpil.mapper.book.BookMapper;
import com.geonpil.util.IsbnUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.geonpil.util.converter.BookConverterUtil.*;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookMapper bookMapper;
    private final ExternalBookApiClient externalBookApiClient;


    public BookDetailViewResponse getBookById(Long bookId) {
        return toDetailView(toDomain(bookMapper.findById(bookId)));
    }

    public Book getOrFetchBookByIsbn(String rawIsbn) {
        String filteredIsbn = extractIsbn13IfExists(rawIsbn);

        Book book;

        // isbn으로 찾고 없으면 books에서 바로 return
        if (filteredIsbn.length() == 13) {
            book = toDomain(bookMapper.findByIsbn13(filteredIsbn));
        } else if (filteredIsbn.length() == 10) {
            book = toDomain(bookMapper.findByIsbn10(filteredIsbn));
        } else {
            throw new IllegalArgumentException("ISBN 길이가 유효하지 않습니다: " + filteredIsbn);
        }

        if(book != null) return book;


        //없으면 isbn으로 api 요청보내서 찾아온다음 books에 insert
        Book fetched = externalBookApiClient.fetchBookByIsbn(filteredIsbn);

        BookEntity fetchedBookEntity = toEntity(fetched);
        fetchedBookEntity.setIsbn(filteredIsbn);
        fetchedBookEntity.setIsbn10(IsbnUtil.extractIsbn10(rawIsbn));
        fetchedBookEntity.setIsbn13(IsbnUtil.extractIsbn13(rawIsbn));
        bookMapper.insertBook(fetchedBookEntity);


        System.out.println("Generated ID: " + fetched.getBookId());


        return fetched;
    }

    private String extractIsbn13IfExists(String rawIsbn) {
        if (rawIsbn == null || rawIsbn.trim().isEmpty()) return null;

        String[] parts = rawIsbn.trim().split(" +"); // 공백 기준 분리 (공백 하나 이상 대응)
        for (String part : parts) {
            if (part.length() == 13) {
                return part.trim(); // 13자리 ISBN 우선 반환
            }
        }

        return parts[0].trim(); // 없으면 첫 번째 (보통 10자리)
    }

}

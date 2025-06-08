package com.geonpil.service.book;

import com.geonpil.domain.Book;
import com.geonpil.dto.bookDetail.BookDetailViewResponse;
import com.geonpil.dto.bookDetail.BookEntity;
import com.geonpil.external.ExternalBookApiClient;
import com.geonpil.mapper.book.BookMapper;
import com.geonpil.util.IsbnUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.geonpil.util.converter.BookConverterUtil.*;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookMapper bookMapper;
    private final ExternalBookApiClient externalBookApiClient;


    public BookDetailViewResponse getBookById(Long bookId) {
        return toDetailView(toDomain(bookMapper.findById(bookId)));
    }

    public BookDetailViewResponse getOrFetchBookByIsbn(String rawIsbn) {
        String filteredIsbn = extractIsbn13IfExists(rawIsbn);

        BookEntity bookEntity = null;

        // isbn으로 찾고 없으면 books에서 바로 return
        if (filteredIsbn.length() == 13) {
            bookEntity = bookMapper.findByIsbn13(filteredIsbn) ;
        } else if (filteredIsbn.length() == 10) {
            bookEntity = bookMapper.findByIsbn10(filteredIsbn);
        } else {
            throw new IllegalArgumentException("ISBN 길이가 유효하지 않습니다: " + filteredIsbn);
        }

        if(bookEntity != null) {
            return toDetailView(toDomain(bookEntity));
        }


        //없으면 isbn으로 api 요청보내서 찾아온다음 books에 insert
        Book fetched = externalBookApiClient.fetchBookByIsbn(filteredIsbn);

        System.out.println("디버그 " + fetched.getBookId());

        BookEntity fetchedBookEntity = toEntity(fetched);
        fetchedBookEntity.setIsbn(filteredIsbn);
        fetchedBookEntity.setIsbn10(IsbnUtil.extractIsbn10(rawIsbn));
        fetchedBookEntity.setIsbn13(IsbnUtil.extractIsbn13(rawIsbn));
        bookMapper.insertBook(fetchedBookEntity);
        BookDetailViewResponse bookDetailViewResponse = toDetailView(toDomain(fetchedBookEntity));


        System.out.println("Generated ID: " + bookDetailViewResponse.getBookId());


        return bookDetailViewResponse;
    }


    public List<BookDetailViewResponse> getLatestReviewedBookById() {
        List<BookEntity> lastestBookEntities = bookMapper.findLastestReviewedBook();


        List<BookDetailViewResponse> lastestBooks = new ArrayList<BookDetailViewResponse>();
        for(BookEntity bookEntity : lastestBookEntities) {
            BookDetailViewResponse book = toDetailView(toDomain(bookEntity));
            lastestBooks.add(book);
        }


        return lastestBooks;
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

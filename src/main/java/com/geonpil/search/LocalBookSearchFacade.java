package com.geonpil.search;

import com.geonpil.dto.bookSearch.BookSearchViewResponse;
import com.geonpil.dto.bookSearch.PopularKeyword;
import com.geonpil.service.book.BookSearchLogService;
import com.geonpil.service.book.BookSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "search.service.use-remote", havingValue = "false", matchIfMissing = true)
public class LocalBookSearchFacade implements BookSearchFacade {

    private final BookSearchService bookSearchService;
    private final BookSearchLogService bookSearchLogService;

    @Override
    public BookSearchViewResponse searchBooks(String query, int page, int size) {
        return bookSearchService.searchBooks(query, page, size);
    }

    @Override
    public List<String> getSuggestions(String prefix, int limit) {
        return bookSearchLogService.getSearchSuggestions(prefix, limit, bookSearchService);
    }

    @Override
    public List<PopularKeyword> getPopularKeywords(int topN) {
        return bookSearchLogService.getPopularKeywords(topN);
    }

    @Override
    public void logSearch(String keyword, Long userId, String ip, String userAgent) {
        bookSearchLogService.logSearch(keyword, userId, ip, userAgent);
    }
}


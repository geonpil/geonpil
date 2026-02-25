package com.geonpil.search;

import com.geonpil.dto.bookSearch.BookSearchViewResponse;
import com.geonpil.dto.bookSearch.PopularKeyword;

import java.util.List;

public interface BookSearchFacade {

    BookSearchViewResponse searchBooks(String query, int page, int size);

    List<String> getSuggestions(String prefix, int limit);

    List<PopularKeyword> getPopularKeywords(int topN);

    void logSearch(String keyword, Long userId, String ip, String userAgent);
}


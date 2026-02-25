package com.geonpil.search;

import com.geonpil.dto.bookSearch.BookSearchViewResponse;
import com.geonpil.dto.bookSearch.PopularKeyword;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;

/**
 * search-service REST API를 호출하는 BookSearchFacade 구현체.
 * search.service.use-remote=true 일 때 사용됩니다.
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "search.service.use-remote", havingValue = "true")
public class RestBookSearchFacade implements BookSearchFacade {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public RestBookSearchFacade(
            RestTemplate restTemplate,
            @Value("${search.service.base-url:http://localhost:8081}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    @Override
    public BookSearchViewResponse searchBooks(String query, int page, int size) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/api/search/books")
                .queryParam("query", query)
                .queryParam("page", page)
                .queryParam("size", size)
                .build()
                .toUriString();
        try {
            return restTemplate.getForObject(url, BookSearchViewResponse.class);
        } catch (Exception e) {
            log.error("search-service 책 검색 실패: query={}, error={}", query, e.getMessage(), e);
            throw new RuntimeException("검색 서비스 호출 실패", e);
        }
    }

    @Override
    public List<String> getSuggestions(String prefix, int limit) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/api/search/suggestions")
                .queryParam("q", prefix)
                .queryParam("limit", limit)
                .build()
                .toUriString();
        try {
            ResponseEntity<List<String>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<String>>() {}
            );
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (Exception e) {
            log.error("search-service 자동완성 실패: prefix={}, error={}", prefix, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<PopularKeyword> getPopularKeywords(int topN) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/api/search/popular")
                .queryParam("topN", topN)
                .build()
                .toUriString();
        try {
            ResponseEntity<List<PopularKeyword>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<PopularKeyword>>() {}
            );
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (Exception e) {
            log.error("search-service 인기검색어 조회 실패: error={}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public void logSearch(String keyword, Long userId, String ip, String userAgent) {
        String url = baseUrl + "/api/search/log";
        LogSearchRequest body = new LogSearchRequest(keyword, userId, ip, userAgent);
        try {
            restTemplate.postForObject(url, body, Void.class);
        } catch (Exception e) {
            log.warn("search-service 검색 로그 저장 실패: keyword={}, error={}", keyword, e.getMessage());
        }
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    private static class LogSearchRequest {
        private String keyword;
        private Long userId;
        private String ip;
        private String userAgent;
    }
}

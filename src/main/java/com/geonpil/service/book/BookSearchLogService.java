package com.geonpil.service.book;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.geonpil.dto.bookSearch.BookSearchLogDoc;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookSearchLogService {

    private static final String INDEX = "book_search_log";

    private final ElasticsearchClient esClient;

    @Async
    public void logSearch(String keyword, Long userId, String ip, String userAgent){
        String normalized = normalizeKeyword(keyword);

        if(normalized == null) {
            log.debug("검색어가 null이거나 비어있어 로깅을 건너뜁니다.");
            return;
        }

        BookSearchLogDoc doc = new BookSearchLogDoc(normalized
                                                    , userId
                                                    , ip
                                                    , trimUserAgent(userAgent)
                                                    , Instant.now());

        try{
            log.debug("Elasticsearch에 검색 로그 저장 시도: keyword={}, userId={}, index={}", normalized, userId, INDEX);
            esClient.index(i -> i
                    .index(INDEX)
                    .document(doc));
            log.debug("검색 로그 저장 성공: keyword={}", normalized);
        } catch (Exception e){
            log.error("검색 로그 저장 실패: keyword={}, error={}", normalized, e.getMessage(), e);
        }

       
    }


    private String normalizeKeyword(String keyword){
        if(keyword == null || keyword.isEmpty()){ return null; }
                String k = keyword.trim();
        if(k.isEmpty()){ return null; }
        if(k.length() > 200) k = k.substring(0, 200);
        return k;
    }

    private String trimUserAgent(String ua){
        if (ua == null) return null;
        ua = ua.trim();
        if(ua.length() > 500) ua = ua.substring(0, 500);
        return ua;
    }

}







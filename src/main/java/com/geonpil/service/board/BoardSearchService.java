package com.geonpil.service.board;

import com.geonpil.domain.BoardDTO;
import com.geonpil.dto.boardSearch.SearchResult;
import com.geonpil.elasticsearch.BoardDocument;
import com.geonpil.mapper.BoardMapper;
import com.geonpil.repository.search.BoardSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.erhlc.NativeSearchQuery;
import org.springframework.data.elasticsearch.client.erhlc.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardSearchService {

    private final BoardSearchRepository boardSearchRepository;
    private final ElasticsearchOperations elasticsearchOperations;
    private final BoardMapper boardMapper;

    public void index(BoardDTO dto) {
        BoardDocument doc = new BoardDocument(dto.getPostId(), dto.getTitle(), dto.getContent(), dto.getBoardCode());
        boardSearchRepository.save(doc);
    }


    public SearchResult<BoardDTO> searchByKeyword(String keyword, int page, int size, Integer boardCode) {
        if (keyword == null || keyword.trim().length() < 1) { // 최소 길이를 1로 낮춤
            return new SearchResult<>(Collections.emptyList(), page, size, 0, 0);
        }

        // 직접 JSON 쿼리를 작성해서 사용 - match 대신 match_phrase_prefix 사용
        String queryJson = String.format("""
            {
              "bool": {
                "must": [
                  { "term": { "boardCode": %d } },
                  { 
                    "bool": {
                      "should": [
                        { "match_phrase_prefix": { "title": { "query": "%s", "slop": 3, "max_expansions": 10 } } },
                        { "match_phrase_prefix": { "content": { "query": "%s", "slop": 3, "max_expansions": 10 } } }
                      ],
                      "minimum_should_match": 1
                    }
                  }
                ]
              }
            }
            """, boardCode, keyword, keyword);

        StringQuery query = new StringQuery(queryJson);
        query.setPageable(PageRequest.of(page - 1, size));

        System.out.println("실행 쿼리: " + queryJson);

        SearchHits<BoardDocument> hits = elasticsearchOperations.search(query, BoardDocument.class);

        List<Long> postIds = hits.getSearchHits().stream()
                .map(hit -> hit.getContent().getPostId())
                .toList();

        // For debug purposes
        System.out.println("Found " + postIds.size() + " documents in ES with boardCode=" + boardCode + " and keyword=" + keyword);
        postIds.forEach(id -> System.out.println("ES Hit: postId=" + id));

        if (postIds.isEmpty()) {
            return new SearchResult<>(Collections.emptyList(), page, size, 0, 0);
        }

        // 이제 postIds로 DB에서 조회
        List<BoardDTO> boards = boardMapper.findBoardsByPostIds(postIds, boardCode);

        // 검색 결과 확인 디버깅
        System.out.println("ES hit count: " + postIds.size() + ", DB result count: " + boards.size());

        long totalHits = hits.getTotalHits();
        int totalPages = (int) Math.ceil((double) totalHits / size);

        System.out.println("디버그 " + totalHits + ", " + totalPages + ", " + postIds.size());

        return new SearchResult<>(boards, page, size, totalHits, totalPages);
    }


    public void indexAllFromDatabase() {
        List<BoardDTO> allPosts = boardMapper.findAllForIndexing();


        for (BoardDTO dto : allPosts) {
            System.out.println("postId = " + dto.getPostId() + ", boardCode = " + dto.getBoardCode());
        }

        List<BoardDocument> docs = allPosts.stream()
                .map(dto -> new BoardDocument(dto.getPostId(), dto.getTitle(), dto.getContent(), dto.getBoardCode()))
                .toList();

        boardSearchRepository.saveAll(docs);
    }
}
package com.geonpil.service.board;

import com.geonpil.domain.BoardDTO;
import com.geonpil.dto.boardSearch.SearchResult;
import com.geonpil.elasticsearch.BoardDocument;
import com.geonpil.mapper.BoardMapper;
import com.geonpil.repository.search.BoardSearchRepository;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
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
        if (keyword == null || keyword.trim().length() < 2) {
            return new SearchResult<>(Collections.emptyList(), page, size, 0, 0);
        }

        Criteria criteria = new Criteria("boardCode").is(boardCode)
                .and(new Criteria("title").matches(keyword)
                .or(new Criteria("content").matches(keyword)));

        Query query = new CriteriaQuery(criteria, PageRequest.of(page - 1, size));

        SearchHits<BoardDocument> hits = elasticsearchOperations.search(query, BoardDocument.class);

        List<Long> postIds = hits.getSearchHits().stream()
                .map(hit -> hit.getContent().getPostId())
                .toList();

        List<BoardDTO> boards = boardMapper.findBoardsByPostIds(postIds, boardCode);

        long totalHits = hits.getTotalHits();
        int totalPages = (int) Math.ceil((double) totalHits / size);

        System.out.println("디벅" + totalHits + "," + totalPages + "," + postIds.size());
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
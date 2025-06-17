package com.geonpil.service.board;

import com.geonpil.domain.BoardDTO;
import com.geonpil.elasticsearch.BoardDocument;
import com.geonpil.mapper.BoardMapper;
import com.geonpil.repository.search.BoardSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.erhlc.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardSearchService {

    private final BoardSearchRepository boardSearchRepository;
    private final ElasticsearchOperations elasticsearchOperations;
    private final BoardMapper boardMapper;

    public void index(BoardDTO dto) {
        BoardDocument doc = new BoardDocument(dto.getPostId(), dto.getTitle(), dto.getContent());

        boardSearchRepository.save(doc);
    }


    public List<BoardDTO> searchByKeyword(String keyword) {
        String queryString = String.format("""
        {
          "multi_match": {
            "query": "%s",
            "fields": ["title", "content"]
          }
        }
    """, keyword);
        Query query = new StringQuery(queryString);
        SearchHits<BoardDocument> hits = elasticsearchOperations.search(query, BoardDocument.class);

        List<Long> postIds = hits.getSearchHits().stream()
                .map(hit -> hit.getContent().getPostId())
                .toList();


        return boardMapper.findBoardsByPostIds(postIds);
    }


    public void indexAllFromDatabase() {
        List<BoardDTO> allPosts = boardMapper.findAllForIndexing();

        List<BoardDocument> docs = allPosts.stream()
                .map(dto -> new BoardDocument(dto.getPostId(), dto.getTitle(), dto.getContent()))
                .toList();

        boardSearchRepository.saveAll(docs);
    }
}
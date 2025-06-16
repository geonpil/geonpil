package com.geonpil.service.board;

import com.geonpil.domain.BoardDTO;
import com.geonpil.elasticsearch.BoardDocument;
import com.geonpil.repository.search.BoardSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.erhlc.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardSearchService {

    private final BoardSearchRepository boardSearchRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    public void index(BoardDTO dto) {
        BoardDocument doc = new BoardDocument();
        doc.setPostId(dto.getPostId());
        doc.setUserId(dto.getUserId());
        doc.setBoardCode(dto.getBoardCode());
        doc.setCategoryId(dto.getCategoryId());
        doc.setTitle(dto.getTitle());
        doc.setContent(dto.getContent());
        doc.setViewCount(dto.getViewCount());
        doc.setLikeCount(dto.getLikeCount());
        doc.setCreatedAt(dto.getCreatedAt());

        boardSearchRepository.save(doc);
    }


    public List<BoardDocument> searchByKeyword(String keyword) {
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

        return hits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }
}
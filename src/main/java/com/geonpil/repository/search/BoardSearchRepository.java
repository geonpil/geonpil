package com.geonpil.repository.search;

import com.geonpil.elasticsearch.BoardDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardSearchRepository extends ElasticsearchRepository<BoardDocument, Long> {
    // 게시판 특화 쿼리 메서드를 여기에 추가 가능
}

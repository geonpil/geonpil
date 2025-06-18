package com.geonpil.repository.search;

import com.geonpil.elasticsearch.BoardDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardSearchRepository extends ElasticsearchRepository<BoardDocument, Long> {
    // 커스텀 쿼리 메서드도 여기에 추가 가능
}
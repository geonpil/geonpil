package com.geonpil.repository.search;

import com.geonpil.elasticsearch.ContestDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContestSearchRepository extends ElasticsearchRepository<ContestDocument, Long> {
    // 공모전 특화 쿼리 메서드를 여기에 추가 가능
}

package com.geonpil.service.test;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ElasticsearchTestService {

    private final ElasticsearchClient elasticsearchClient;

    public void ping() {
        try {
            boolean response = elasticsearchClient.ping().value();
            System.out.println("Elasticsearch 연결 성공 여부: " + response);
        } catch (Exception e) {
            System.err.println("Elasticsearch 연결 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }
}


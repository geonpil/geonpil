package com.geonpil.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.geonpil.repository.search")
public class ElasticSearchConfig {

    @Bean
    public ElasticsearchClient elasticsearchClient() {
        RestClient restClient = RestClient.builder(
                new HttpHost("localhost", 9200, "http"))
                .build();

        ElasticsearchTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper());

        return new ElasticsearchClient(transport);
    }
}

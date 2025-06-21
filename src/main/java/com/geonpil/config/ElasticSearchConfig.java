package com.geonpil.config;

import com.geonpil.elasticsearch.BoardDocument;
import com.geonpil.elasticsearch.ContestDocument;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;
import org.springframework.data.elasticsearch.core.convert.MappingElasticsearchConverter;
import org.springframework.data.elasticsearch.core.mapping.SimpleElasticsearchMappingContext;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;

import java.util.Set;

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

    @Bean
    public SimpleElasticsearchMappingContext elasticsearchMappingContext() {
        SimpleElasticsearchMappingContext mappingContext = new SimpleElasticsearchMappingContext();

        // 명시적으로 엔티티 클래스 등록
        mappingContext.setInitialEntitySet(Set.of(
            BoardDocument.class,
            ContestDocument.class
        ));

        return mappingContext;
    }

    @Bean
    public ElasticsearchConverter elasticsearchConverter(SimpleElasticsearchMappingContext mappingContext) {
        return new MappingElasticsearchConverter(mappingContext);
    }
}

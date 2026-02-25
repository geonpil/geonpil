package com.geonpil.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.JsonpMapper;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;

@Configuration
public class ElasticsearchClientConfig {

    @Value("${elasticsearch.host}")
    private String host;

    @Value("${elasticsearch.username:}")
    private String username;

    @Value("${elasticsearch.password:}")
    private String password;

    @Bean
    public RestClient restClient() {
        HttpHost httpHost = HttpHost.create(host);
        RestClientBuilder builder = RestClient.builder(httpHost);

        // username과 password가 비어있지 않을 때만 인증 추가
        if (username != null && !username.trim().isEmpty() && 
            password != null && !password.trim().isEmpty()) {
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(
                AuthScope.ANY,
                new UsernamePasswordCredentials(username, password)
            );

            builder.setHttpClientConfigCallback(httpClientBuilder -> 
                httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
            );
        }

        return builder.build();
    }

    @Bean
    public JsonpMapper jsonpMapper(ObjectMapper objectMapper) {
        // Spring Boot가 관리하는 ObjectMapper(=JavaTimeModule 적용된)를 ES에 그대로 사용
        return new JacksonJsonpMapper(objectMapper);
    }

    @Bean
    public ElasticsearchTransport elasticsearchTransport(RestClient restClient, JsonpMapper jsonpMapper) {
        return new RestClientTransport(restClient, jsonpMapper);
    }

    @Bean
    public ElasticsearchClient elasticsearchClient(ElasticsearchTransport transport) {
        return new ElasticsearchClient(transport);
    }
}
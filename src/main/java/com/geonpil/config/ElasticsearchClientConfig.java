package com.geonpil.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.ssl.SSLContextBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.JsonpMapper;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;

import javax.net.ssl.SSLContext;

@Configuration
public class ElasticsearchClientConfig {

    private static final Logger log = LoggerFactory.getLogger(ElasticsearchClientConfig.class);

    @Value("${elasticsearch.host}")
    private String host;

    @Value("${elasticsearch.username:}")
    private String username;

    @Value("${elasticsearch.password:}")
    private String password;

    @Value("${elasticsearch.ssl.insecure:false}")
    private boolean sslInsecure;

    @Bean
    public RestClient restClient() throws Exception {
        HttpHost httpHost = HttpHost.create(host);
        log.info("Elasticsearch 연결: host={}, sslInsecure={}, auth={}",
                host, sslInsecure, (username != null && !username.isEmpty()) ? "설정됨" : "없음");

        RestClientBuilder builder = RestClient.builder(httpHost);

        builder.setHttpClientConfigCallback(httpClientBuilder -> {
            // HTTPS + 개발용 인증서 무시 (self-signed 신뢰)
            // HTTPS + (개발용 인증서 무시 또는 host.docker.internal 접속 시 CN 불일치)
            boolean needInsecure = "https".equalsIgnoreCase(httpHost.getSchemeName())
                    && (sslInsecure || (host != null && host.contains("host.docker.internal")));
            if (needInsecure) {
                try {
                    SSLContext sslContext = SSLContextBuilder.create()
                            .loadTrustMaterial(null, (chain, authType) -> true)
                            .build();
                    httpClientBuilder.setSSLContext(sslContext);
                    httpClientBuilder.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE);
                } catch (Exception e) {
                    throw new RuntimeException("Elasticsearch SSL insecure 설정 실패", e);
                }
            }
            // username과 password가 비어있지 않을 때만 인증 추가
            if (username != null && !username.trim().isEmpty() &&
                    password != null && !password.trim().isEmpty()) {
                CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                credentialsProvider.setCredentials(
                        AuthScope.ANY,
                        new UsernamePasswordCredentials(username, password)
                );
                httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
            }
            return httpClientBuilder;
        });

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
package com.geonpil.service.book;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.geonpil.dto.bookSearch.BookSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


import org.springframework.http.HttpHeaders;

@Service
@RequiredArgsConstructor
public class BookSearchService {

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public BookSearchResponse searchBooks(String query,int page, int size) {
        String url = UriComponentsBuilder
                .fromHttpUrl("https://dapi.kakao.com/v3/search/book")
                .queryParam("query", query)
                .queryParam("page", page)
                .queryParam("size", size)
                .build()
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        String body = response.getBody();


        try{
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Optional: 날짜 포맷 개선

            JsonNode root = mapper.readTree(body);

            JsonNode docs = root.path("documents");
            JsonNode metaNode = root.path("meta");

            BookSearchResponse result = mapper.readValue(body, BookSearchResponse.class);

            return result;

        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 파싱 실패", e);
        }
    }
}

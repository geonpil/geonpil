package com.geonpil.external;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.geonpil.domain.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import org.springframework.http.HttpHeaders;


@RequiredArgsConstructor
@Component
public class ExternalBookApiClient {

    @Value("${kakao.api.key}")
    private String kakaoApiKey;
    private final RestTemplate restTemplate = new RestTemplate();


    public Book fetchBookByIsbn(String filteredIsbn){

        String url = UriComponentsBuilder
                .fromHttpUrl("https://dapi.kakao.com/v3/search/book")
                .queryParam("query", filteredIsbn)
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
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            JsonNode root = mapper.readTree(body);

            JsonNode docs = root.path("documents");
            if(docs.isArray() && docs.size() > 0) {
                Book book = mapper.treeToValue(docs.get(0), Book.class);
                book.setIsbn(filteredIsbn);
                return book;
            } else {
                throw new RuntimeException("도서를 찾을 수 없습니다.");
            }


        }catch (JsonProcessingException e){
            throw new RuntimeException("JSON 파싱 실패", e);
        }
    }



}

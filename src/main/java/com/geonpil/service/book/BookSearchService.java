package com.geonpil.service.book;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.geonpil.domain.Book;
import com.geonpil.dto.response.BookSearchResponse;
import com.geonpil.dto.response.Meta;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


import org.springframework.http.HttpHeaders;

import java.util.Arrays;
import java.util.List;

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

            JsonNode root = mapper.readTree(body);

            JsonNode docs = root.path("documents");
            JsonNode metaNode = root.path("meta");

            List<Book> books = new java.util.ArrayList<>();

            for(JsonNode bookJson : docs){
                ObjectNode bookNode = (ObjectNode) bookJson.deepCopy();
                JsonNode authorsNode = bookNode.remove("authors"); // ← authors 제거

                Book book = mapper.treeToValue(bookNode, Book.class); // 이제 에러 없음


                //  authos 필드만 수동으로 List<String> -> String 변환
                List<String> authorList = mapper.convertValue(bookJson.get("authors"), new TypeReference<List<String>>() {});
                book.setAuthors(String.join(", ",authorList ));


                books.add(book);
            }


            Meta meta = mapper.treeToValue(metaNode,Meta.class);


            BookSearchResponse result = new BookSearchResponse();
            result.setDocuments(books);
            result.setMeta(meta);


            return result;

        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 파싱 실패", e);
        }
    }
}

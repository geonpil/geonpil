package com.geonpil.service.board;

import com.geonpil.domain.BoardDTO;
import com.geonpil.dto.boardSearch.SearchResult;
import com.geonpil.elasticsearch.BoardDocument;
import com.geonpil.mapper.board.BoardMapper;
import com.geonpil.repository.search.BoardSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardSearchService {

    private final BoardSearchRepository boardSearchRepository;
    private final ElasticsearchOperations elasticsearchOperations;
    private final BoardMapper boardMapper;

    public void index(BoardDTO dto) {
        BoardDocument doc = new BoardDocument(dto.getPostId(), dto.getTitle(), dto.getContent(), dto.getBoardCode(), dto.getCategoryId());
        boardSearchRepository.save(doc);
    }


    public SearchResult<BoardDTO> searchByKeyword(String keyword, int page, int size, Integer boardCode, String categoryIds, String searchType) {

        // 검색 유형이 null인 경우 기본값 설정
        if (searchType == null) {
            searchType = "titleContent"; // 기본 검색 유형을 "titleContent"로 설정
            System.out.println("검색 유형이 null이어서 기본값 'titleContent'로 설정됨");
        }

        // categoryIds 처리 - null 체크 개선
        List<Long> categoryIdList = null;
        if (categoryIds != null && !categoryIds.trim().isEmpty() && !categoryIds.equals("0")) {
            try {
                categoryIdList = Arrays.stream(categoryIds.split(","))
                        .filter(s -> !s.trim().isEmpty())
                        .map(s -> Long.parseLong(s.trim()))
                        .collect(Collectors.toList());
            } catch (NumberFormatException e) {
                System.out.println("카테고리 ID 변환 중 오류 발생: " + e.getMessage());
                // 오류 발생 시 빈 리스트로 처리
                categoryIdList = Collections.emptyList();
            }
        }

        // 쿼리 JSON 구성
        StringBuilder queryJson = new StringBuilder();
        queryJson.append("{\n")
                .append("  \"bool\": {\n");

        // 게시판 코드는 filter 절로 이동 (must에서 filter로 변경)
        queryJson.append("    \"filter\": [\n")
                .append("      { \"term\": { \"boardCode\": " + boardCode + " } }\n");

        // 카테고리 필터 추가
        if (categoryIdList != null && !categoryIdList.isEmpty()) {
            queryJson.append("      ,{ \"terms\": { \"categoryId\": [");
            for (int i = 0; i < categoryIdList.size(); i++) {
                queryJson.append(categoryIdList.get(i));
                if (i < categoryIdList.size() - 1) {
                    queryJson.append(",");
                }
            }
            queryJson.append("] } }\n");
        }

        queryJson.append("    ]");  // filter 절 종료

        // 키워드 검색 조건은 키워드가 있을 때만 추가
        if (keyword != null && !keyword.trim().isEmpty()) {
            queryJson.append(",\n    \"must\": [\n");

            switch (searchType) {
                case "title":
                    // 제목만 검색
                    queryJson.append("      { \"match_phrase_prefix\": { \"title\": { \"query\": \"" + keyword + "\", \"slop\": 3, \"max_expansions\": 10 } } }\n");
                    break;

                case "content":
                    // 내용만 검색
                    queryJson.append("      { \"match_phrase_prefix\": { \"content\": { \"query\": \"" + keyword + "\", \"slop\": 3, \"max_expansions\": 10 } } }\n");
                    break;

                case "all":
                case "titleContent":
                default:
                    // 제목+내용 검색 (기본 검색 방식)
                    queryJson.append("      { \"bool\": {\n")
                          .append("          \"should\": [\n")
                          .append("            { \"match_phrase_prefix\": { \"title\": { \"query\": \"" + keyword + "\", \"slop\": 3, \"max_expansions\": 10 } } },\n")
                          .append("            { \"match_phrase_prefix\": { \"content\": { \"query\": \"" + keyword + "\", \"slop\": 3, \"max_expansions\": 10 } } }\n")
                          .append("          ],\n")
                          .append("          \"minimum_should_match\": 1\n")
                          .append("        }\n")
                          .append("      }\n");
                    break;
            }

            queryJson.append("    ]"); // must 절 종료
        }

        queryJson.append("\n  }\n") // bool 종료
               .append("}");       // 전체 쿼리 종료

        StringQuery query = new StringQuery(queryJson.toString());
        query.setPageable(PageRequest.of(page - 1, size));

        System.out.println("검색 타입: " + searchType);
        System.out.println("실행 쿼리: " + queryJson);

        SearchHits<BoardDocument> hits = elasticsearchOperations.search(query, BoardDocument.class);

        List<Long> postIds = hits.getSearchHits().stream()
                .map(hit -> hit.getContent().getPostId())
                .toList();

        // For debug purposes
        System.out.println("Found " + postIds.size() + " documents in ES with boardCode=" + boardCode + " and keyword=" + keyword);
        postIds.forEach(id -> System.out.println("ES Hit: postId=" + id));

        if (postIds.isEmpty()) {
            return new SearchResult<>(Collections.emptyList(), page, size, 0, 0);
        }

        // 이제 postIds로 DB에서 조회
        List<BoardDTO> boards = boardMapper.findBoardsByPostIds(postIds);

        // 검색 결과 확인 디버깅
        System.out.println("ES hit count: " + postIds.size() + ", DB result count: " + boards.size());

        long totalHits = hits.getTotalHits();
        int totalPages = (int) Math.ceil((double) totalHits / size);

        System.out.println("디버그 " + totalHits + ", " + totalPages + ", " + postIds.size());

        return new SearchResult<>(boards, page, size, totalHits, totalPages);
    }


    public void indexAllFromDatabase() {
        List<BoardDTO> allPosts = boardMapper.findAllForIndexing();

        if (allPosts.isEmpty()) {
            System.out.println("색인할 게시글이 없습니다.");
            return;
        }

        List<BoardDocument> docs = allPosts.stream()
                .map(dto -> {
                    return new BoardDocument(dto.getPostId(), dto.getTitle(), dto.getContent(), dto.getBoardCode(), dto.getCategoryId());
                })
                .toList();

        boardSearchRepository.saveAll(docs);
        System.out.println("총 " + docs.size() + "개 게시글 색인 완료");
    }
}
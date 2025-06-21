package com.geonpil.dto.boardSearch;

import com.geonpil.domain.BoardDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
public class SearchResult<BoardDTO> {
    private List<BoardDTO> content; // 검색결과 목록
    private int page; // 현재 페이지 번호
    private int size; // 페이지당 아이템 수
    private long totalHits;// 총 검색 결과 수
    private int totalPages; // 총 페이지 수

}

package com.geonpil.dto.boardSearch;

import com.geonpil.domain.BoardDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
public class SearchResult<BoardDTO> {
    private List<BoardDTO> content;
    private int page;
    private int size;
    private long totalHits;
    private int totalPages;

}

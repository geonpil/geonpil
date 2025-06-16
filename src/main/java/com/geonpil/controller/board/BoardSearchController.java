package com.geonpil.controller.board;

import com.geonpil.domain.BoardDTO;
import com.geonpil.elasticsearch.BoardDocument;
import com.geonpil.service.board.BoardSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search/board")
@RequiredArgsConstructor
public class BoardSearchController {

    private final BoardSearchService boardSearchService;

    @PostMapping("/index")
    public String indexBoard(@RequestBody BoardDTO dto) {
        boardSearchService.index(dto);
        return "색인 완료!";
    }


    @GetMapping
    public List<BoardDocument> searchBoard(@RequestParam String keyword) {
        return boardSearchService.searchByKeyword(keyword);
    }
}
package com.geonpil.controller.board;

import com.geonpil.domain.BoardDTO;
import com.geonpil.service.board.BoardSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
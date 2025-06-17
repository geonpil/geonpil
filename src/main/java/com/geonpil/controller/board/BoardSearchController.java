package com.geonpil.controller.board;

import com.geonpil.domain.BoardDTO;
import com.geonpil.service.board.BoardSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/search/board")
@RequiredArgsConstructor
public class BoardSearchController {

    private final BoardSearchService boardSearchService;

    @ResponseBody
    @PostMapping("/index")
    public String indexBoard(@RequestBody BoardDTO dto) {
        boardSearchService.index(dto);
        return "색인 완료!";
    }

    @ResponseBody
    @PostMapping("/index-all")
    public ResponseEntity<String> indexAll() {
        boardSearchService.indexAllFromDatabase();
        return ResponseEntity.ok("모든 게시글 색인 완료!");
    }

    @GetMapping
    public String searchBoard(@RequestParam String keyword, Model model) {
        List<BoardDTO> results = boardSearchService.searchByKeyword(keyword);
        System.out.println("키워드 :" + keyword);
        model.addAttribute("posts", results);
        return "board/_post-list-fragment::postListFragment";
    }
}
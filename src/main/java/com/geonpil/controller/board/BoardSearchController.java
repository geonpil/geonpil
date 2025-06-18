package com.geonpil.controller.board;

import com.geonpil.domain.BoardDTO;
import com.geonpil.domain.PageResult;
import com.geonpil.dto.boardSearch.SearchResult;
import com.geonpil.dto.commons.PageInfo;
import com.geonpil.service.board.BoardSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.geonpil.util.PaginationUtil.buildPageInfo;

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
    public String searchBoard(@RequestParam String keyword,
                              @RequestParam(defaultValue = "1") int page,
                              @RequestParam Integer boardCode,
                              Model model) {
        int size = 10;

        SearchResult<BoardDTO> results = boardSearchService.searchByKeyword(keyword, page, size, boardCode);

        PageInfo pageInfo = buildPageInfo(page, results.getTotalPages(),10,keyword);

        System.out.println("키워드 :" + keyword);
        System.out.println("게시판 코드 :" + boardCode);
        model.addAttribute("posts", results.getContent());
        model.addAttribute("action", "board");

        model.addAttribute("pageInfo", pageInfo);

        return "board/_search-result-fragment::searchResultFragment";
    }
}
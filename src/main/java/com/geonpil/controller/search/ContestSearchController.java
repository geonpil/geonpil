package com.geonpil.controller.search;

import com.geonpil.domain.ContestPost;
import com.geonpil.dto.boardSearch.SearchResult;
import com.geonpil.dto.commons.PageInfo;
import com.geonpil.service.contest.ContestSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static com.geonpil.util.PaginationUtil.buildPageInfo;

@Controller
@RequestMapping("/api/search/contest")
@RequiredArgsConstructor
public class ContestSearchController {

    private final ContestSearchService contestSearchService;

    @ResponseBody
    @PostMapping("/index")
    public String indexContest(@RequestBody ContestPost dto) {
        contestSearchService.index(dto);
        return "공모전 색인 완료!";
    }

    @ResponseBody
    @PostMapping("/index-all")
    public ResponseEntity<String> indexAll() {
        contestSearchService.indexAllFromDatabase();
        return ResponseEntity.ok("모든 공모전 색인 완료!");
    }

    @GetMapping
    public String searchContest(@RequestParam String keyword,
                              @RequestParam(defaultValue = "1") int page,
                              @RequestParam Integer boardCode,
                              @RequestParam(required = false, defaultValue = "") String categoryIds,
                              @RequestParam(required = false, defaultValue = "false") boolean isClosedIncluded,
                              @RequestParam(required = false, defaultValue = "recent") String sort,
                              @RequestParam(required = false, defaultValue = "searchType") String searchType,
                              Model model) {
        int size = 8;

        SearchResult<ContestPost> results = contestSearchService.searchByKeyword(
            keyword, page, size, boardCode, categoryIds, isClosedIncluded, sort, searchType);

        // PageInfo 객체 생성
        PageInfo pageInfo = buildPageInfo(page, results.getTotalPages(), 10, keyword);

        // 모델에 검색 결과 추가
        model.addAttribute("contests", results.getContent());
        model.addAttribute("pageInfo", pageInfo);
        model.addAttribute("action", "contest");

        // 검색 파라미터도 함께 전달하여 페이징 시 유지되도록 함
        model.addAttribute("boardCode", boardCode);
        model.addAttribute("sort", sort);
        model.addAttribute("isClosedIncluded", isClosedIncluded);
        model.addAttribute("categoryIds", categoryIds);
        model.addAttribute("keyword", keyword);

        // 통합된 프래그먼트 반환 (목록과 페이지네이션 함께)
        return "contest/_contest-combined-fragment::combinedContestFragment";
    }
}

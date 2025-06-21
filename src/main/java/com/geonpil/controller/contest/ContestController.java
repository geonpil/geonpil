package com.geonpil.controller.contest;

import com.geonpil.domain.*;
import com.geonpil.dto.commons.PageInfo;
import com.geonpil.service.BoardService;
import com.geonpil.service.CategoryService;
import com.geonpil.service.ContestService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static com.geonpil.util.PaginationUtil.buildPageInfo;


@Controller
@RequestMapping("/contest")
public class ContestController {
    private final ContestService contestService;
    private final CategoryService categoryService;
    private final BoardService boardService;


    public ContestController(ContestService contestService, CategoryService categoryService, BoardService boardService) {
        this.contestService = contestService;
        this.categoryService = categoryService;
        this.boardService = boardService;
    }

    // 공모전 게시판 리스트
    @GetMapping("/list")
    public String list(Model model,
                       @RequestParam(defaultValue = "1") int page,
                       @RequestParam("boardCode") int boardCode) {
        int pageSize = 8;
        List<Long> categoryIds = new ArrayList<>();
        String sort = "recent";
        boolean isClosedIncluded = false;


        List<ContestPost> contests = contestService.findContestsByPage(page, pageSize, categoryIds, sort, isClosedIncluded);
        
        //카테고리목록 가져오기
        List<Category> categories = categoryService.getCategoriesByBoardCode(boardCode);

        // 전체페이지 가지고 오기
        int totalPages = contestService.getTotalPageCount(pageSize, boardCode, categoryIds, isClosedIncluded);

        PageInfo pageInfo = buildPageInfo(page, totalPages, 5,"");

        System.out.println("디버그 :" + pageInfo.getTotalPages());

        model.addAttribute("categories", categories);
        model.addAttribute("contests", contests);
        model.addAttribute("currentPage", page);
        model.addAttribute("boardCode",boardCode);

        model.addAttribute("pageInfo", pageInfo);
        model.addAttribute("action", "contest");

        return "contest/list";
    }

    // 글쓰기 페이지
    @GetMapping("/write")
    public String writeForm(Model model, @RequestParam("boardCode") int boardCode) {
        List<Category> categories = categoryService.getCategoriesByBoardCode(boardCode);

        model.addAttribute("boardCode", boardCode);
        model.addAttribute("categories", categories);

        return "contest/write";
    }

    // 글쓰기 상세
    @GetMapping("/detail/{id}")
    public String contestDetail(@PathVariable Long id,
                                @RequestParam(value = "boardCode", required = false) Integer boardCode,
                                Model model) {
        ContestPost post = contestService.findContestById(id);
        boardService.increaseViewCount(id);
        model.addAttribute("boardCode", boardCode);
        model.addAttribute("post", post);
        return "contest/detail";
    }

    // 글쓰기 수정
    @GetMapping("/edit/{id}")
    public String contestEdit(@PathVariable Long id,
                                @RequestParam(value = "boardCode", required = false) Integer boardCode,
                                Model model) {
        ContestPost post = contestService.findContestById(id);
        List<Category> categories = categoryService.getCategoriesByBoardCode(boardCode);
        model.addAttribute("categories", categories);
        model.addAttribute("boardCode", boardCode);
        model.addAttribute("post", post);
        return "contest/edit";
    }


    @GetMapping("/list/fragment")
    public String listFragment(@RequestParam("boardCode") int boardCode,
                               @RequestParam(required = false) List<Long> categoryIds,
                               @RequestParam(value = "page", defaultValue = "1") int page,
                               @RequestParam("sort") String sort,
                               @RequestParam("isClosedIncluded") boolean isClosedIncluded,
                               Model model) {
        int pageSize = 8;

        List<ContestPost> contests = contestService.findContestsByPage(page, pageSize, categoryIds, sort, isClosedIncluded);


        int totalPages = contestService.getTotalPageCount(pageSize, boardCode, categoryIds, isClosedIncluded);
        System.out.println("프래그먼트:" + totalPages);

        model.addAttribute("contests", contests);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("boardCode",boardCode);


        return "contest/_contest-list-fragment :: contestListFragment";
    }

    @GetMapping("/list/fragment/pagination")
    public String getPaginationFragment(@RequestParam("boardCode") int boardCode,
                                       @RequestParam(required = false) List<Long> categoryIds,
                                       @RequestParam(value = "page", defaultValue = "1") int page,
                                       @RequestParam("sort") String sort,
                                       @RequestParam("isClosedIncluded") boolean isClosedIncluded,
                                       Model model) {
        int pageSize = 8;

        int totalPages = contestService.getTotalPageCount(pageSize, boardCode, categoryIds, isClosedIncluded);

        PageInfo pageInfo = buildPageInfo(page, totalPages, 5,"");
        System.out.println("디버그 :" + pageInfo.getTotalPages());

        model.addAttribute("pageInfo", pageInfo);
        model.addAttribute("action", "contest");

        return "commons/_pagination-fragment :: paginationFragment";
    }

    /**
     * 공모전 목록과 페이지네이션을 한번에 반환하는 통합 메서드
     */
    @GetMapping("/list/combined-fragment")
    public String getCombinedFragment(@RequestParam("boardCode") int boardCode,
                                     @RequestParam(required = false) List<Long> categoryIds,
                                     @RequestParam(value = "page", defaultValue = "1") int page,
                                     @RequestParam("sort") String sort,
                                     @RequestParam("isClosedIncluded") boolean isClosedIncluded,
                                     Model model) {
        int pageSize = 8;

        // 공모전 목록 조회
        List<ContestPost> contests = contestService.findContestsByPage(page, pageSize, categoryIds, sort, isClosedIncluded);

        // 총 페이지 수 계산
        int totalPages = contestService.getTotalPageCount(pageSize, boardCode, categoryIds, isClosedIncluded);

        // 페이지 정보 생성
        PageInfo pageInfo = buildPageInfo(page, totalPages, 5,"");

        // 모델에 데이터 추가
        model.addAttribute("contests", contests);
        model.addAttribute("pageInfo", pageInfo);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("boardCode", boardCode);
        model.addAttribute("action", "contest");

        // 검색 시 사용할 파라미터들도 추가
        model.addAttribute("sort", sort);
        model.addAttribute("isClosedIncluded", isClosedIncluded);
        model.addAttribute("categoryIds", categoryIds);

        return "contest/_contest-combined-fragment :: combinedContestFragment";
    }
}
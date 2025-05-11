package com.geonpil.controller;

import com.geonpil.domain.*;
import com.geonpil.resolver.BoardNameResolver;
import com.geonpil.security.CustomUserDetails;
import com.geonpil.service.BoardService;
import com.geonpil.service.CategoryService;
import com.geonpil.service.CommentService;
import com.geonpil.service.ContestService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;


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

        List<ContestPost> contests = contestService.findContestsByPage(page, pageSize);
        int totalPages = contestService.getTotalPageCount(pageSize, boardCode);
        model.addAttribute("contests", contests);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("boardCode",boardCode);
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

        System.out.println("디버그 : " + categories.get(0));
        System.out.println("디버그2 : " + post.getCategoryId());
        model.addAttribute("categories", categories);
        model.addAttribute("boardCode", boardCode);
        model.addAttribute("post", post);
        return "contest/edit";
    }
}
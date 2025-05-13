package com.geonpil.controller;

import com.geonpil.service.BoardService;
import com.geonpil.service.ContestService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    private final BoardService boardService;
    private final ContestService contestService;

    public MainController(BoardService boardService, ContestService contestService) {
        this.boardService = boardService;
        this.contestService = contestService;
    }

    @GetMapping("/")
    public String mainPage(Model model) {
        model.addAttribute("communityPosts", boardService.getLatestPosts(1, 10));
        model.addAttribute("challengePosts", boardService.getLatestPosts(2, 10));
        model.addAttribute("contestPost", contestService.findLatestContest(6));
        return "main"; // templates/main.htmlurn "main"; // templates/main.html
    }
}
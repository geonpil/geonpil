package com.geonpil.controller;

import com.geonpil.service.BoardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    private final BoardService boardService;

    public MainController(BoardService boardService) {
        this.boardService = boardService;
    }

    @GetMapping("/")
    public String mainPage(Model model) {
        model.addAttribute("communityPosts", boardService.getLatestPosts(1, 10));
        model.addAttribute("contestPosts", boardService.getLatestPosts(2, 10));
        model.addAttribute("challengePosts", boardService.getLatestPosts(3, 10));
        return "main"; // templates/main.htmlurn "main"; // templates/main.html
    }
}
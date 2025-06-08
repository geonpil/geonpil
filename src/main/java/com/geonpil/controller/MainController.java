package com.geonpil.controller;

import com.geonpil.service.BoardService;
import com.geonpil.service.ContestService;
import com.geonpil.service.book.BookService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    private final BoardService boardService;
    private final ContestService contestService;
    private final BookService bookService;

    public MainController(BoardService boardService, ContestService contestService, BookService bookService) {
        this.boardService = boardService;
        this.contestService = contestService;
        this.bookService = bookService;
    }

    @GetMapping("/")
    public String mainPage(Model model) {
        model.addAttribute("communityPosts", boardService.getLatestPosts(1, 10));
        model.addAttribute("challengePosts", boardService.getLatestPosts(2, 10));
        model.addAttribute("contestPost", contestService.findLatestContest(6));

        System.out.println("디버그2 :" + bookService.getLatestReviewedBookById().get(0).getThumbnail());

        model.addAttribute("lastestReviewedBooks", bookService.getLatestReviewedBookById());

        return "main"; // templates/main.htmlurn "main"; // templates/main.html
    }
}
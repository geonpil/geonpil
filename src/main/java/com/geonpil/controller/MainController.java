package com.geonpil.controller;

import com.geonpil.service.board.BoardService;
import com.geonpil.service.contest.ContestService;
import com.geonpil.service.admin.BookPickService;
import com.geonpil.service.book.BookService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    private final BoardService boardService;
    private final ContestService contestService;
    private final BookService bookService;
    private final BookPickService bookPickService;

    public MainController(BoardService boardService, ContestService contestService, BookService bookService, BookPickService bookPickService) {
        this.boardService = boardService;
        this.contestService = contestService;
        this.bookService = bookService;
        this.bookPickService = bookPickService;
    }

    @GetMapping("/")
    public String mainPage(Model model) {
        model.addAttribute("communityPosts", boardService.getLatestPosts(1, 10));
        model.addAttribute("challengePosts", boardService.getLatestPosts(2, 10));
        model.addAttribute("contestPost", contestService.findLatestContest(6));

        model.addAttribute("lastestReviewedBooks", bookService.getLatestReviewedBookById());
        model.addAttribute("bookPicks", bookPickService.getAllBookPicks());

        return "main"; // templates/main.htmlurn "main"; // templates/main.html
    }
}
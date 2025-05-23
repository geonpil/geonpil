package com.geonpil.controller;

import com.geonpil.domain.Book;
import com.geonpil.service.BookSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


import java.util.List;

@Controller
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class BookSearchController {

    private final BookSearchService bookSearchService;

    @GetMapping
    public String searchBooks(
            @RequestParam String query,
            Model model) {
        List<Book> books = bookSearchService.searchBooks(query);

        model.addAttribute("books", books);
        model.addAttribute("query", query);

        return "search/search-result";
    }
}

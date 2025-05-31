package com.geonpil.controller.book;

import com.geonpil.dto.bookSearch.BookSearchResponse;
import com.geonpil.dto.bookSearch.Meta;
import com.geonpil.service.book.BookSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class BookSearchController {

    private final BookSearchService bookSearchService;

    @GetMapping("")
    public String searchBooks(
            @RequestParam String query,
            @RequestParam(defaultValue = "1") int page,
            Model model) {
        prepareModel(query, page, 10, model, true, true);
        return "book/search/search-result";
    }



    @GetMapping("/fragment/result")
    public String getSearchFragment(
            @RequestParam String query,
            @RequestParam(defaultValue = "1") int page,
            Model model) {

        prepareModel(query, page, 10, model, true, false);
        return "book/search/_result-fragment :: resultFragment";

    }




    @GetMapping("/fragment/pagination")
    public String getPaginationFragment(
            @RequestParam String query,
            @RequestParam(defaultValue = "1") int page,
            Model model) {

        prepareModel(query, page, 10, model, false, true);
        return "book/search/_pagination-fragment :: paginationFragment";

    }



    private BookSearchResponse prepareModel(String query, int page, int pageSize, Model model, boolean includeBooks, boolean includePagination) {
        BookSearchResponse result = bookSearchService.searchBooks(query, page, pageSize);
        Meta meta = result.getMeta();

        int groupSize = 10;
        int currentGroup = (int) Math.ceil((double) page / groupSize);
        int totalPages = (int) Math.ceil((double) meta.getPageable_count() / pageSize);
        int startPage = (currentGroup - 1) * groupSize + 1;
        int endPage = Math.min(startPage + groupSize - 1, totalPages);

        if (includeBooks) {
            model.addAttribute("books", result.getDocuments());
            model.addAttribute("pageableCount", meta.getPageable_count());
        }

        if (includePagination) {
            model.addAttribute("startPage", startPage);
            model.addAttribute("endPage", endPage);
            model.addAttribute("hasPrevGroup", startPage > 1);
            model.addAttribute("hasNextGroup", endPage < totalPages);
            model.addAttribute("pageableCount", meta.getPageable_count());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
        }

        model.addAttribute("query", query);

        return result;
    }
}

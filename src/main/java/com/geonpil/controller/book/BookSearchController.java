package com.geonpil.controller.book;

import com.geonpil.dto.bookSearch.BookSearchResponse;
import com.geonpil.dto.bookSearch.BookSearchViewResponse;
import com.geonpil.dto.bookSearch.Meta;
import com.geonpil.dto.commons.PageInfo;
import com.geonpil.service.book.BookSearchService;
import com.geonpil.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static com.geonpil.util.PaginationUtil.buildPageInfo;

@Controller
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class BookSearchController {

    private final BookSearchService bookSearchService;

    int pageSize = 15;

    @GetMapping("")
    public String searchBooks(
            @RequestParam String query,
            @RequestParam(defaultValue = "1") int page,
            Model model) {
        prepareModel(query, page, pageSize, model, true, true);

        return "book/search/search-result";
    }



    @GetMapping("/fragment/result")
    public String getSearchFragment(
            @RequestParam String query,
            @RequestParam(defaultValue = "1") int page,
            Model model) {

        prepareModel(query, page, pageSize, model, true, false);
        return "book/search/_result-fragment :: resultFragment";

    }




    @GetMapping("/fragment/pagination")
    public String getPaginationFragment(
            @RequestParam String query,
            @RequestParam(defaultValue = "1") int page,
            Model model) {

        prepareModel(query, page, pageSize, model, false, true);
        return "commons/_pagination-fragment :: paginationFragment";

    }



    private BookSearchViewResponse prepareModel(String query, int page, int pageSize, Model model, boolean includeBooks, boolean includePagination) {
        BookSearchViewResponse result = bookSearchService.searchBooks(query, page, pageSize);
        Meta meta = result.getMeta();

        int totalPages = (int) Math.ceil((double) meta.getPageable_count() / pageSize);
        int groupSize = 10;

        if (includeBooks) {
            model.addAttribute("books", result.getBooks());
            model.addAttribute("pageableCount", meta.getPageable_count());
        }

        if (includePagination) {
            PageInfo pageInfo = PaginationUtil.buildPageInfo(page, totalPages, groupSize, query);
            model.addAttribute("pageInfo", pageInfo);
            model.addAttribute("action", "book");
        }

        model.addAttribute("query", query);

        return result;
    }
}

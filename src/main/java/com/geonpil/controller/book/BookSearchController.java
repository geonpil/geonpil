package com.geonpil.controller.book;

import com.geonpil.dto.bookSearch.BookSearchResponse;
import com.geonpil.dto.bookSearch.BookSearchViewResponse;
import com.geonpil.dto.bookSearch.Meta;
import com.geonpil.dto.commons.PageInfo;
import com.geonpil.security.AppUserInfo;
import com.geonpil.service.book.BookSearchLogService;
import com.geonpil.service.book.BookSearchService;
import com.geonpil.util.PaginationUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

import static com.geonpil.util.PaginationUtil.buildPageInfo;

@Controller
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class BookSearchController {

    private final BookSearchService bookSearchService;
    private final BookSearchLogService bookSearchLogService;

    int pageSize = 15;

    @GetMapping("")
    public String searchBooks(
            @RequestParam String query,
            @RequestParam(defaultValue = "1") int page,
            Model model,
            HttpServletRequest request,
            @AuthenticationPrincipal AppUserInfo user) {
        prepareModel(query, page, pageSize, model, true, true);

        logSearch(query, request, user);
        
        return "book/search/search-result";
    }



    @GetMapping("/fragment/result")
    public String getSearchFragment(
            @RequestParam String query,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam String mode,
            Model model,
            HttpServletRequest request,
            @AuthenticationPrincipal AppUserInfo user) {

        model.addAttribute("mode", mode);
        prepareModel(query, page, pageSize, model, true, false);

        logSearch(query, request, user);

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

    /**
     * 검색어 자동완성 API
     * @param q 검색어 접두사
     * @return 자동완성 제안 리스트
     */
    @GetMapping("/suggestions")
    @ResponseBody
    public ResponseEntity<List<String>> getSearchSuggestions(
            @RequestParam String q,
            @RequestParam(defaultValue = "10") int limit) {
        
        List<String> suggestions = bookSearchLogService.getSearchSuggestions(q, limit);
        return ResponseEntity.ok(suggestions);
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


    private void logSearch(String query, 
                          HttpServletRequest request,
                          AppUserInfo user){
        String ip = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");
        Long userId = (user != null) ? user.getId() : null;
        
        bookSearchLogService.logSearch(query, userId, ip, userAgent);
    }


    private String getClientIp(HttpServletRequest request) {
        // 프록시/로드밸런서 환경 고려
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();

    }
}

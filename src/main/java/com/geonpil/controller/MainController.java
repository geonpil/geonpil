package com.geonpil.controller;

import com.geonpil.domain.admin.Banners;
import com.geonpil.service.board.BoardService;
import com.geonpil.service.contest.ContestService;
import com.geonpil.service.admin.BannerService;
import com.geonpil.service.admin.BookPickService;
import com.geonpil.service.book.BookService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class MainController {

    private final BoardService boardService;
    private final ContestService contestService;
    private final BookService bookService;
    private final BookPickService bookPickService;
    private final BannerService bannerService;

    public MainController(BoardService boardService, ContestService contestService, BookService bookService, BookPickService bookPickService, BannerService bannerService) {
        this.boardService = boardService;
        this.contestService = contestService;
        this.bookService = bookService;
        this.bookPickService = bookPickService;
        this.bannerService = bannerService;
    }

    @GetMapping("/")
    public String mainPage(Model model) {
        model.addAttribute("communityPosts", boardService.getLatestPosts(1, 10));
        model.addAttribute("challengePosts", boardService.getLatestPosts(2, 10));
        model.addAttribute("contestPost", contestService.findLatestContest(6));

        model.addAttribute("lastestReviewedBooks", bookService.getLatestReviewedBookById());
        model.addAttribute("bookPicks", bookPickService.getAllBookPicks());

        List<Banners> allBanners = bannerService.getAllBanners();
        String typeMain = "main";
        String typeSub = "sub";
        // 타입만으로 구분 (노출 여부는 일단 적용; DB is_visible 매핑 문제 시 여기서 .filter(Banners::isVisible) 제거해 보며 확인)
        Banners mainBanner = allBanners.stream()
                .filter(b -> typeMain.equalsIgnoreCase(b.getType() != null ? b.getType().trim() : ""))
                .filter(Banners::isVisible)
                .findFirst()
                .orElse(null);
        List<Banners> subBanners = allBanners.stream()
                .filter(b -> typeSub.equalsIgnoreCase(b.getType() != null ? b.getType().trim() : ""))
                .filter(Banners::isVisible)
                .collect(Collectors.toList());
        model.addAttribute("mainBanner", mainBanner);
        model.addAttribute("subBanners", subBanners);

        return "main";
    }
}
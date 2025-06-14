package com.geonpil.controller.admin;

import com.geonpil.domain.Category;
import com.geonpil.domain.ContestPost;
import com.geonpil.domain.admin.BookPick;
import com.geonpil.dto.commons.PageInfo;
import com.geonpil.service.BoardService;
import com.geonpil.service.CategoryService;
import com.geonpil.service.ContestService;
import com.geonpil.service.admin.BookPickService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static com.geonpil.util.PaginationUtil.buildPageInfo;


@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final BookPickService bookPickService;

    // 공모전 게시판 리스트
    @GetMapping("/main")
    public String list(Model model) {
        return "/admin/admin";
    }


    @PostMapping("/book-picks/add")
    public String addBookPicks(@ModelAttribute BookPick bookPick){

        bookPickService.saveBookPick(bookPick);

        return "redirect:/admin/main";
    }

}
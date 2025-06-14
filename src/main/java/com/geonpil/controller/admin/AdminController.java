package com.geonpil.controller.admin;

import com.geonpil.domain.admin.BookPick;
import com.geonpil.dto.bookPick.BookPickWithBookInfo;
import com.geonpil.service.admin.BookPickService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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

    @GetMapping("/book-picks/fragment/add")
    public String getAddBookPickFragment(Model model) {
        
        return "admin/_book-pick-add-fragment :: bookPickAddFormFragment";
    }


    @GetMapping("/book-picks/fragment/delete")
    public String getDelBookPickFragment(Model model) {

        List<BookPickWithBookInfo> bookPicks = bookPickService.getAllBookPicks();

        model.addAttribute("bookPicks", bookPicks);

        return "admin/_book-pick-delete-fragment :: bookPickDeleteFormFragment";
    }

}
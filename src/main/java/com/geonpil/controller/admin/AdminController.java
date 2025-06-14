package com.geonpil.controller.admin;

import com.geonpil.domain.admin.BookPick;
import com.geonpil.service.admin.BookPickService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;



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
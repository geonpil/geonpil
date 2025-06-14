package com.geonpil.controller.admin;

import com.geonpil.domain.admin.BookPick;
import com.geonpil.service.admin.BookPickService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/admin")
@RequiredArgsConstructor
public class AdminRestController {

    private final BookPickService bookPickService;


    @PostMapping("/book-picks")
    public ResponseEntity<String> addBookPicks(@RequestBody BookPick bookPick){

        System.out.println("isbn 확인" + bookPick.getIsbn());
        bookPickService.saveBookPick(bookPick);

        return ResponseEntity.ok("입력완료");
    }

    @PutMapping("/book-picks/{id}")
    public ResponseEntity<String> delBookPicks(@PathVariable("id") String bookPickId){

        bookPickService.deleteBookPick(bookPickId);

        return ResponseEntity.ok("입력완료");
    }
}
package com.geonpil.controller.contest;

import com.geonpil.domain.ContestPost;
import com.geonpil.security.AppUserInfo;
import com.geonpil.service.contest.ContestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("api/contest")
public class ContestRestController {


    private final ContestService contestService;

    public ContestRestController(ContestService contestService) {
        this.contestService = contestService;
    }

    @PostMapping
    public ResponseEntity<String> create(@RequestBody ContestPost contest
                                        , @AuthenticationPrincipal AppUserInfo user) {
        contest.setUserId(user.getId());
        contestService.saveContest(contest, contest.getCategoryIds()); // board + contest_post 저장
        return ResponseEntity.status(HttpStatus.CREATED).body("success");
    }


    @PutMapping("/update")
    public ResponseEntity<String> update(@RequestBody ContestPost contest
            , @AuthenticationPrincipal AppUserInfo user) {
        contest.setUserId(user.getId());
        contestService.updateContestPost(contest, user.getId()); // board + contest_post 저장
        return ResponseEntity.status(HttpStatus.CREATED).body("success");
    }

}
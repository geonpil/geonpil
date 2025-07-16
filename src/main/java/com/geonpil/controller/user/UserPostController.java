package com.geonpil.controller.user;

import com.geonpil.domain.BoardDTO;
import com.geonpil.domain.PageResult;
import com.geonpil.domain.user.User;
import com.geonpil.dto.commons.PageInfo;
import com.geonpil.security.AppUserInfo;
import com.geonpil.service.board.BoardService;
import com.geonpil.service.user.UserService;
import com.geonpil.util.PaginationUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class UserPostController {

    private final BoardService boardService;
    private final UserService userService;

    // 내가 쓴 글 보기
    @GetMapping("/my-posts")
    @PreAuthorize("isAuthenticated()")
    public String myPosts(@AuthenticationPrincipal AppUserInfo userInfo, @RequestParam(defaultValue = "1") int page, Model model) {
        User user = userService.findUserById(userInfo.getId()); 
        PageResult<BoardDTO> pageResult = boardService.findByUserId(user.getId(), page);

        int totalCount = boardService.findByUserIdCount(user.getId());

        PageInfo pageInfo = PaginationUtil.buildPageInfo(page, pageResult.getTotalPages(), 10, null);

        // 현재 경로를 링크 기반 페이지네이션에 활용하기 위한 baseUrl 전달
        String baseUrl = "/my-posts";

        model.addAttribute("baseUrl", baseUrl);
        model.addAttribute("pageInfo", pageInfo);
        model.addAttribute("action", "user-posts");
        model.addAttribute("owner", user);
        model.addAttribute("posts", pageResult.getPosts());
        model.addAttribute("totalCount", totalCount);
        
        return "user/post/user-posts";
    }

    // 특정 사용자의 글 보기
    @GetMapping("/users/{userId}/posts")
    public String userPosts(@PathVariable Long userId
            , @RequestParam(defaultValue = "1") int page
                , Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            return "redirect:/"; // 또는 404 페이지로
        }
        PageResult<BoardDTO> pageResult = boardService.findByUserId(userId, page);

        int totalCount = boardService.findByUserIdCount(userId);

        PageInfo pageInfo = PaginationUtil.buildPageInfo(page, pageResult.getTotalPages(), 10, null);

        // 현재 경로를 링크 기반 페이지네이션에 활용하기 위한 baseUrl 전달
        String baseUrl = "/users/" + userId + "/posts";

        model.addAttribute("baseUrl", baseUrl);
        model.addAttribute("pageInfo", pageInfo);
        model.addAttribute("action", "user-posts");
        model.addAttribute("owner", user);
        model.addAttribute("posts", pageResult.getPosts());
        model.addAttribute("totalCount", totalCount);


        return "user/post/user-posts";
    }
} 
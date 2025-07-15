package com.geonpil.controller.user;

import com.geonpil.domain.user.User;
import com.geonpil.security.AppUserInfo;
import com.geonpil.service.board.BoardService;
import com.geonpil.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class UserPostController {

    private final BoardService boardService;
    private final UserService userService;

    // 내가 쓴 글 보기
    @GetMapping("/my-posts")
    @PreAuthorize("isAuthenticated()")
    public String myPosts(@AuthenticationPrincipal AppUserInfo userInfo, Model model) {
        User user = userService.findUserById(userInfo.getId());
        model.addAttribute("owner", user);
        model.addAttribute("posts", boardService.findByUserId(user.getId()));
        return "user/post/user-posts";
    }

    // 특정 사용자의 글 보기
    @GetMapping("/users/{userId}/posts")
    public String userPosts(@PathVariable Long userId, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            return "redirect:/"; // 또는 404 페이지로
        }
        
        model.addAttribute("owner", user);
        model.addAttribute("posts", boardService.findByUserId(userId));
        return "user/post/user-posts";
    }
} 
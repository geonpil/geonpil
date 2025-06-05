package com.geonpil.controller.user;

import com.geonpil.domain.user.User;
import com.geonpil.security.AppUserInfo;
import com.geonpil.service.user.UserService;
import com.geonpil.service.user.WithdrawlService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MypageController {

    private final UserService userService;


    //마이페이지 화면
    @GetMapping
    public String myPage(@AuthenticationPrincipal AppUserInfo userInfo, Model model) {
        User user = userService.findUserById(userInfo.getId());
        model.addAttribute("user", user);
        return "user/mypage/mypage";  // ex) templates/user/mypage.html
    }



    @GetMapping("/fragment/myInfo")
    public String myInfoFragment(Model model, @AuthenticationPrincipal AppUserInfo user) {
        model.addAttribute("user", userService.findUserById(user.getId()));
        return "user/mypage/_myInfo-fragment :: myInfoFragment";
    }

    @GetMapping("/fragment/password")
    public String passwordFragment(Model model, @AuthenticationPrincipal AppUserInfo user) {
        model.addAttribute("user", userService.findUserById(user.getId()));
        return "user/mypage/_edit-password-fragment :: editPasswordFragment";
    }

    @GetMapping("/fragment/nickname")
    public String nicknameFragment(Model model, @AuthenticationPrincipal AppUserInfo user) {
        model.addAttribute("user", userService.findUserById(user.getId()));
        return "user/mypage/_edit-myInfo-fragment :: editMyInfoFragment";
    }



}

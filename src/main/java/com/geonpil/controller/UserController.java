package com.geonpil.controller;

import com.geonpil.domain.User;
import com.geonpil.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.geonpil.service.MailService;

import java.util.Optional;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;

    // 회원가입 폼 페이지
    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("user", new User());
        return "signup"; // signup.html
    }
    
    

    
    // 회원가입 처리
    @PostMapping("/signup")
    public String processSignup(@ModelAttribute User user,
                                @RequestParam String passwordConfirm,
                                Model model) {
        if (!user.getPassword().equals(passwordConfirm)) {
            model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "signup";
        }

        boolean result = userService.register(user);
        if (!result) {
            model.addAttribute("error", "이미 사용 중인 이메일 또는 닉네임입니다.");
            return "signup";
        }

        return "redirect:/signup-success";
    }

    // 회원가입 성공 페이지
    @GetMapping("/signup-success")
    public String signupSuccess() {
        return "signup-success";
    }



    //로그인 화면
    @GetMapping("/login")
    public String showLoginForm(HttpServletRequest request, HttpSession session) {
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.contains("/login")) {
            session.setAttribute("redirectAfterLogin", referer);
        }
        return "login"; // templates/login.html 보여주기
    }


}

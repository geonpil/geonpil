package com.geonpil.controller;

import com.geonpil.domain.User;
import com.geonpil.service.UserService;
import jakarta.mail.MessagingException;
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

/*    // 이메일 중복 체크
    @GetMapping("/check-email")
    @ResponseBody
    public String checkEmail(@RequestParam String email) {
        System.out.println("email:" + email);
        boolean exists = userService.existsByEmail(email);
        return exists ? "duplicate" : "available";
    }

    // 닉네임 중복 체크
    @GetMapping("/check-nickname")
    @ResponseBody
    public String checkNickname(@RequestParam String nickname) {
        boolean exists = userService.existsByNickname(nickname);
        return exists ? "duplicate" : "available";
    }*/
/*
    // 메일 전송
    @PostMapping("/send-verification-email")
    @ResponseBody
    public String sendVerificationEmail(@RequestParam String email, HttpSession session) {
        String verificationCode = UUID.randomUUID().toString().substring(0, 6);
        mailService.sendEmail(
                email,
                "[건필] 이메일 인증 코드 안내",
                "<h3>인증 코드: " + verificationCode + "</h3>"
        );

        session.setAttribute("emailVerificationCode", verificationCode); // 👈 세션에 저장
        return "success";  // 더 이상 코드 자체는 클라이언트에 안 넘김
    }*/

/*    // 인증코드 확인
    @PostMapping("/verify-code")
    @ResponseBody
    public String verifyCode(@RequestParam String code, HttpSession session) {
        String savedCode = (String) session.getAttribute("emailVerificationCode");

        if (savedCode != null && savedCode.equals(code)) {
            session.removeAttribute("emailVerificationCode"); // 인증 완료되면 코드 삭제
            return "success";
        } else {
            return "fail";
        }
    }*/


    //로그인 화면
    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; // templates/login.html 보여주기
    }
//
//    //로그인처리
//    @PostMapping("/login")
//    public String processLogin(@RequestParam String email,
//                               @RequestParam String password,
//                               Model model,
//                               HttpSession session) {
//        Optional<User> user = userService.findByEmail(email);
//
//        if (user == null || !passwordEncoder.matches(password, user.getClass())) {
//            model.addAttribute("error", "이메일 또는 비밀번호가 일치하지 않습니다.");
//            return "login"; // 다시 로그인 페이지
//        }
//
//        // 로그인 성공 -> 세션에 저장
//        session.setAttribute("loginUser", user);
//        return "redirect:/"; // 로그인 성공하면 메인페이지로
//    }

//    @PostMapping("/logout")
//    public String logout(HttpSession session) {
//        session.invalidate();  // 세션 완전 초기화 (로그아웃)
//        return "redirect:/";   // 메인페이지로
//    }


}

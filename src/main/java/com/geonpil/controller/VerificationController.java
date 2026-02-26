package com.geonpil.controller;

import com.geonpil.domain.user.User;
import com.geonpil.service.MailService;
import com.geonpil.service.user.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/verify")
@RequiredArgsConstructor
public class VerificationController {

    private final UserService userService;

    @Autowired(required = false)
    private MailService mailService;

    @GetMapping("/find-password")
    public String showFindPasswordForm() {
        return "account/find-password"; // templates/account/find-password.html
    }


    // 이메일 중복 체크
    @GetMapping("/email")
    @ResponseBody
    public String checkEmail(@RequestParam String email) {
        boolean exists = userService.existsByEmail(email);
        return exists ? "duplicate" : "available";
    }

    // 닉네임 중복 체크
    @GetMapping("/nickname")
    @ResponseBody
    public String checkNickname(@RequestParam String nickname) {
        boolean exists = userService.existsByNickname(nickname);
        return exists ? "duplicate" : "available";
    }

    // 회원가입용 인증코드 전송 (서비스에 위임)
    @PostMapping("/send-code")
    @ResponseBody
    public String sendVerificationEmail(@RequestParam String email, HttpSession session) {
        if (mailService == null) {
            return "mail_unavailable";
        }
        String code = mailService.sendSignupVerificationCode(email);
        session.setAttribute("emailVerificationCode", code);
        return "success";
    }

    // 회원가입 인증코드 검증
    @PostMapping("/code")
    @ResponseBody
    public String verifyCode(@RequestParam String code, HttpSession session) {
        String savedCode = (String) session.getAttribute("emailVerificationCode");
        if (savedCode != null && savedCode.equals(code)) {
            session.removeAttribute("emailVerificationCode");
            return "success";
        }
        return "fail";
    }

    // 비밀번호 재설정용 인증코드 전송
    @PostMapping("/find/send-code")
    @ResponseBody
    public String sendResetCode(@RequestParam String email, HttpSession session) throws MessagingException {
        if (mailService == null) {
            return "mail_unavailable";
        }
        Optional<User> user = userService.findByEmail(email);
        if (user.isEmpty()) {
            return "not_found";
        }

        String code = mailService.sendVerificationCode(email);
        session.setAttribute("findPasswordCode", code);
        return code;
    }

    // 비밀번호 재설정 시작 - 세션에 이메일 저장
    @PostMapping("/find/start-reset")
    @ResponseBody
    public String startReset(@RequestParam String email, HttpSession session) {
        session.setAttribute("resetEmail", email);
        return "ok";
    }

    // 비밀번호 재설정 페이지
    @GetMapping("/find/reset-password")
    public String showResetPassword(HttpSession session) {
        if (session.getAttribute("resetEmail") == null) {
            return "redirect:/login";
        }
        return "account/reset-password";
    }

    // 비밀번호 재설정 처리
    @PostMapping("/find/reset-password")
    public String resetPassword(@RequestParam String password, HttpSession session, Model model) {
        String email = (String) session.getAttribute("resetEmail");
        if (email == null) {
            return "redirect:/login";
        }

        Optional<User> user = userService.findByEmail(email);
        if (user.isEmpty()) {
            model.addAttribute("error", "사용자를 찾을 수 없습니다.");
            return "account/reset-password";
        }

        userService.updatePassword(email, password);
        session.removeAttribute("resetEmail");
        return "redirect:/login?resetSuccess=true";
    }
}

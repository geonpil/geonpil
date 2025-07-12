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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final WithdrawlService withdrawlService;

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
    public String showLoginForm(HttpServletRequest request
            , HttpSession session
            , @RequestParam(value="from", required = false) String from
    ) {
        
        //회원가입에서 로그인 후 redirect시 메인화면으로
        if ("signup".equals(from)) {
            session.removeAttribute("SPRING_SECURITY_SAVED_REQUEST");
        }

        return "login"; // templates/login.html 보여주기
    }


    //회원탈퇴 화면
    @GetMapping("/withdraw")
    public String withdrawForm() {
        return "user/withdrawal/withdrawal";  // ex) templates/user/mypage.html
    }

    //회원 탈퇴 처리
    @PostMapping("/withdraw")
    public String handleWithdraw(@AuthenticationPrincipal AppUserInfo userInfo,
                                 @RequestParam(value = "reasons", required = false) List<String> reasons,
                                 @RequestParam(required = false) String feedback,
                                 HttpServletRequest request,
                                 HttpServletResponse response,
                                 RedirectAttributes redirectAttributes) {

        try {
            withdrawlService.process(userInfo.getId(), reasons, feedback);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/withdraw";
        }

        // ✅ 세션 무효화 및 로그아웃 처리
        request.getSession().invalidate(); // 세션 삭제
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(request, response, null);

        return "redirect:/withdrawal-complete";
    }


    //회원 탈퇴 완료
    @GetMapping("/withdrawal-complete")
    public String withdrawComplete() {
        return "user/withdrawal/withdrawal-complete";
    }


}

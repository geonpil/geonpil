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

    // 약관 동의 페이지
    @GetMapping("/signup")
    public String showSignupTerms() {
        return "account/signup-terms"; // templates/account/signup-terms.html
    }

    // 약관 조회 페이지 (팝업용)
    @GetMapping("/terms")
    public String showTermsView() {
        return "account/terms-view"; // templates/account/terms-view.html
    }

    // 회원가입 폼 페이지 (약관 동의 후 진입)
    @GetMapping("/signup/form")
    public String showSignupForm(@RequestParam(value = "agreed", required = false) Boolean agreed,
                                 HttpSession session,
                                 Model model) {
        // 약관 동의 여부 확인
        if (Boolean.TRUE.equals(agreed)) {
            session.setAttribute("agreedTerms", true);
        }

        // 세션에 동의 정보가 없으면 약관 페이지로 리다이렉트
        if (session.getAttribute("agreedTerms") == null) {
            return "redirect:/signup";
        }

        model.addAttribute("user", new User());
        return "account/signup"; // signup.html
    }


    // 회원가입 처리
    @PostMapping("/signup")
    public String processSignup(@ModelAttribute User user,
                                @RequestParam String passwordConfirm,
                                HttpSession session,
                                Model model) {

        // 약관 동의 세션 체크
        if (session.getAttribute("agreedTerms") == null) {
            return "redirect:/signup";
        }

        if (!user.getPassword().equals(passwordConfirm)) {
            model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "account/signup";
        }

        boolean result = userService.register(user);
        if (!result) {
            model.addAttribute("error", "이미 사용 중인 이메일 또는 닉네임입니다.");
            return "account/signup";
        }

        // 동의 정보 세션에서 제거
        session.removeAttribute("agreedTerms");

        return "redirect:/signup-success";
    }

    // 회원가입 성공 페이지
    @GetMapping("/signup-success")
    public String signupSuccess() {
        return "account/signup-success";
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

        return "account/login"; // templates/login.html 보여주기
    }


    //회원탈퇴 화면
    @GetMapping("/withdraw")
    public String withdrawForm() {
        return "account/withdrawal";  // ex) templates/user/mypage.html
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
        return "account/withdrawal-complete";
    }


}

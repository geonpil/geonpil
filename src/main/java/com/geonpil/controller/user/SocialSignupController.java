package com.geonpil.controller.user;

import com.geonpil.domain.user.User;
import com.geonpil.dto.user.SocialProfile;
import com.geonpil.mapper.user.UserMapper;
import com.geonpil.security.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/signup")
@RequiredArgsConstructor
public class SocialSignupController {

    private final UserMapper userMapper;

    @GetMapping("/social")
    public String showSocialSignupForm(HttpSession session, Model model) {
        SocialProfile profile = (SocialProfile) session.getAttribute("OAUTH_PROFILE");
        
        if (profile == null) {
            // 세션에 프로필이 없으면 로그인 페이지로 리다이렉트
            return "redirect:/login";
        }
        
        model.addAttribute("profile", profile);
        return "account/social-signup";
    }

    @PostMapping("/social")
    public String completeSocialSignup(@SessionAttribute("OAUTH_PROFILE") SocialProfile profile,
                                       @RequestParam String nickname,
                                       @RequestParam(required = false) String agreedTerms,
                                       HttpSession session,
                                       Model model) {
        
        // 약관 동의 확인
        if (!"true".equals(agreedTerms)) {
            model.addAttribute("error", "이용약관에 동의해주세요.");
            model.addAttribute("profile", profile);
            return "account/social-signup";
        }
        
        // 닉네임 검증
        if (nickname == null || nickname.trim().length() < 2 || nickname.trim().length() > 20) {
            model.addAttribute("error", "닉네임은 2자 이상 20자 이하로 입력해주세요.");
            model.addAttribute("profile", profile);
            return "account/social-signup";
        }
        
        // 닉네임 중복 확인
        if (userMapper.existsByNickname(nickname.trim())) {
            model.addAttribute("error", "이미 사용 중인 닉네임입니다.");
            model.addAttribute("profile", profile);
            return "account/social-signup";
        }
        
        try {
            // DB에 사용자 저장
            User newUser = new User(
                profile.getEmail(),
                nickname.trim(),
                profile.getProvider(),
                profile.getProviderId(),
                List.of("ROLE_USER")
            );
            userMapper.insertSocialUser(newUser);
            
            // 세션에서 임시 프로필 제거
            session.removeAttribute("OAUTH_PROFILE");
            
            // SecurityContext에 새로운 Authentication 설정 (ROLE_USER로 업그레이드)
            Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
            CustomOAuth2User currentUser = (CustomOAuth2User) currentAuth.getPrincipal();
            
            CustomOAuth2User newAuthUser = new CustomOAuth2User(
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
                currentUser.getAttributes(),
                currentUser.getNameAttributeKey(),
                newUser.getNickname(),
                newUser.getId()
            );
            
            Authentication newAuth = new UsernamePasswordAuthenticationToken(
                newAuthUser,
                currentAuth.getCredentials(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
            );
            
            SecurityContextHolder.getContext().setAuthentication(newAuth);
            
            return "redirect:/?welcome=true";
            
        } catch (Exception e) {
            model.addAttribute("error", "회원가입 중 오류가 발생했습니다: " + e.getMessage());
            model.addAttribute("profile", profile);
            return "account/social-signup";
        }
    }
}
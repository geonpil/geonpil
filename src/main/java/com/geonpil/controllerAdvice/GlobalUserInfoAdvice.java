package com.geonpil.controllerAdvice;

import com.geonpil.dto.bookSearch.PopularKeyword;
import com.geonpil.security.CustomOAuth2User;
import com.geonpil.security.CustomUserDetails;
import com.geonpil.service.book.BookSearchLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@Component
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalUserInfoAdvice {

    private final BookSearchLogService bookSearchLogService;

    @ModelAttribute("nickname")
    public String getNickname(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetails userDetails) {
            return userDetails.getNickname();
        }

        if (principal instanceof CustomOAuth2User customOAuth2User) {
            return customOAuth2User.getNickname();
        }

        return null;
    }

    @ModelAttribute("popularKeywords")
    public List<PopularKeyword> getPopularKeywords() {
        try {
            return bookSearchLogService.getPopularKeywords(10);
        } catch (Exception e) {
            // 에러 발생 시 빈 리스트 반환 (로그는 서비스에서 처리)
            return List.of();
        }
    }

}
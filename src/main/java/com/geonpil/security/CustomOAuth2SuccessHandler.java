package com.geonpil.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {
        
        // 신규 소셜 사용자인지 확인 (ROLE_PRE_SIGNUP 권한 체크)
        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PRE_SIGNUP"))) {
            redirectStrategy.sendRedirect(request, response, "/signup/social");
            return;
        }
        
        SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
        if (savedRequest != null) {
            new DefaultRedirectStrategy().sendRedirect(request, response, savedRequest.getRedirectUrl());
            return;
        }

        HttpSession session = request.getSession(false);
        if (session != null) {
            String redirectUrl = (String) session.getAttribute("redirectAfterLogin");
            if (redirectUrl != null) {
                session.removeAttribute("redirectAfterLogin");
                redirectStrategy.sendRedirect(request, response, redirectUrl);
                return;
            }
        }

        // fallback: 기본 경로로 이동
        redirectStrategy.sendRedirect(request, response, "/");
    }
}

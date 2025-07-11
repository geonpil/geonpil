package com.geonpil.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class CustomAuthFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        String errorMessage = "아이디 또는 비밀번호가 올바르지 않습니다.";
        String exMessage;
        if (exception instanceof OAuth2AuthenticationException oauthEx) {
            exMessage = oauthEx.getError().getErrorCode();
        } else {
            exMessage = exception.getMessage();
        }

        if (exMessage != null && exMessage.startsWith("RECOVER_REQUIRED:")) {
            // exMessage 형식: RECOVER_REQUIRED:123
            String userId = exMessage.substring("RECOVER_REQUIRED:".length());
            System.out.println("🔥 [로그인 실패] 탈퇴 계정 – 복구 필요, userId=" + userId);
            response.sendRedirect("/recover?userId=" + userId);
            return;
        } else if (exMessage != null && exMessage.contains("탈퇴한 계정")) {
            errorMessage = "탈퇴한 계정입니다.";
        } else if (exMessage != null && exMessage.contains("사용자를 찾을 수 없습니다")) {
            errorMessage = "존재하지 않는 계정입니다.";
        }

        System.out.println("🔥 [로그인 실패] errorMessage: " + errorMessage);
        response.sendRedirect("/login?error=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8));
    }
}
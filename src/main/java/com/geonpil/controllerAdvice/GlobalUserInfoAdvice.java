package com.geonpil.controllerAdvice;

import com.geonpil.security.CustomOAuth2User;
import com.geonpil.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@Component
@ControllerAdvice
public class GlobalUserInfoAdvice {

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

}
package com.geonpil.service;

import com.geonpil.domain.User;
import com.geonpil.mapper.UserMapper;
import com.geonpil.security.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserMapper userMapper;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(request);

        // 네이버는 사용자 정보가 response 안에 들어 있음
        Map<String, Object> attributes = oAuth2User.getAttributes();
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        String email = (String) response.get("email");
        String nickname = (String) response.get("nickname");
        String naverId = (String) response.get("id");


        // 회원 조회 or 가입 처리
        Optional<User> userOpt = userMapper.findByProviderAndProviderId("naver", naverId);
        User user;
        if (userOpt.isEmpty()) {
            user = new User(email, nickname, "naver", naverId, "user");  // 소셜 로그인용 생성자 사용
            userMapper.insertSocialUser(user);
        }  else {
            user = userOpt.get();
        }


        return new CustomOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                response, // nickname이 직접 포함된 Map
                "id",     // nameAttributeKey
                user.getNickname(),
                user.getId()// DB에 저장된 nickname 사용
        );
    }
}

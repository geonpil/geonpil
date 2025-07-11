package com.geonpil.service;

import com.geonpil.domain.user.User;
import com.geonpil.mapper.user.UserMapper;
import com.geonpil.security.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserMapper userMapper;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(request);

        String registrationId = request.getClientRegistration().getRegistrationId(); // "kakao" 또는 "naver"

        // 네이버는 사용자 정보가 response 안에 들어 있음
        Map<String, Object> attributes = oAuth2User.getAttributes();
        Map<String, Object> flatAttributes = new HashMap<>();


        String email = null;
        String nickname = null;
        String providerId = null;


        if (registrationId.equals("kakao")) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
            email = (String) kakaoAccount.get("email");
            nickname = (String) profile.get("nickname");
            providerId = String.valueOf(attributes.get("id"));
        } else if (registrationId.equals("naver")) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            email = (String) response.get("email");
            nickname = (String) response.get("nickname");
            providerId = (String) response.get("id");

            flatAttributes.put("email", email);
            flatAttributes.put("nickname", nickname);
            flatAttributes.put("id", providerId);

        }

        // 회원 조회 or 가입 처리
        Optional<User> userOpt = userMapper.findByProviderAndProviderId(registrationId, providerId);
        User user;
        if (userOpt.isPresent()) {
            user = userOpt.get();
            if (user.isDeleted()) {
                throw new OAuth2AuthenticationException("RECOVER_REQUIRED:" + user.getId());
            }
            user.setRoles(userMapper.findRolesById(user.getId()));
        } else {
            user = new User(email, nickname, registrationId, providerId, List.of("ROLE_USER"));  // 2️⃣ 신규 소셜 가입
            userMapper.insertSocialUser(user);
        }

        userMapper.findRolesById(user.getId());

        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role)) // ex) "ROLE_ADMIN"
                .collect(Collectors.toList());


        return new CustomOAuth2User(
                authorities,
                flatAttributes, // nickname이 직접 포함된 Map
                "id",     // nameAttributeKey
                user.getNickname(),
                user.getId()// DB에 저장된 nickname 사용
        );
    }

    // 계정 복구는 AccountRecoveryController에서 별도로 수행한다.
}

//package com.geonpil.oauth;
//
//import com.geonpil.domain.user.User;
//import com.geonpil.mapper.user.UserMapper;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.stereotype.Service;
//import java.util.Map;
//
//@Service
//@RequiredArgsConstructor
//public class CustomOAuth2UserService extends DefaultOAuth2UserService {
//
//    private final UserMapper userMapper;
//
//    @Override
//    public OAuth2User loadUser(OAuth2UserRequest request) {
//        OAuth2User oAuth2User = super.loadUser(request);
//        Map<String, Object> attributes = oAuth2User.getAttributes();
//
//        // 네이버 로그인 사용자 정보 추출
//        OAuthAttributes oAuthAttributes = OAuthAttributes.ofNaver(attributes);
//        String email = oAuthAttributes.getEmail();
//
//        // 사용자 존재 여부 확인
//        User existingUser = userMapper.findByEmail(email);
//        if (existingUser == null) {
//            // 자동 회원가입
//            User newUser = new User(null, email, oAuthAttributes.getNickname(), oAuthAttributes.getProvider());
//            userMapper.insertUser(newUser);
//        }
//
//        return oAuth2User; // 로그인은 Security가 알아서 처리
//    }
//}
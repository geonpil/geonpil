//package com.geonpil.oauth;
//
//import java.util.Map;
//
//public class OAuthAttributes {
//    private final String email;
//    private final String nickname;
//    private final String provider;
//
//    public OAuthAttributes(String email, String nickname, String provider) {
//        this.email = email;
//        this.nickname = nickname;
//        this.provider = provider;
//    }
//
//    public static OAuthAttributes ofNaver(Map<String, Object> attributes) {
//        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
//
//        return new OAuthAttributes(
//                (String) response.get("email"),
//                (String) response.get("name"),
//                "naver"
//        );
//    }
//
//    public String getEmail() { return email; }
//    public String getNickname() { return nickname; }
//    public String getProvider() { return provider; }
//}

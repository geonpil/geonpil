package com.geonpil.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocialProfile {
    private String email;
    private String nickname; // 소셜에서 가져온 기본 닉네임
    private String provider; // "kakao", "naver"
    private String providerId; // 소셜 고유 ID
}
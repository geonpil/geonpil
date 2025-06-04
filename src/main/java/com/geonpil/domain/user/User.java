package com.geonpil.domain.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Long id;
    private String password;     // 비밀번호 (암호화 예정)
    private String nickname;     // 닉네임
    private String email;        // 이메일 주소
    private String role;         // 권한 (예: USER, ADMIN 등)
    private String provider;
    private String providerId;
    private LocalDateTime createdAt;
    private boolean isDeleted;  //삭제 여부

    public User(String email, String nickname, String provider, String providerId, String role) {
        this.email = email;
        this.nickname = nickname;
        this.providerId = providerId;
        this.provider = provider;
        this.role = role;
        this.isDeleted = false;
    }

}





package com.geonpil.service;

import com.geonpil.domain.User;
import com.geonpil.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public boolean register(User user) {
        if (userMapper.existsByEmail(user.getEmail())) {
            return false;
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("user");
        userMapper.insertLocalUser(user);
        return true;
    }

    //이메일 중복체크
    public boolean existsByEmail(String email) {
        return userMapper.existsByEmail(email);
    }

    //닉네임 중복체크    
    public boolean existsByNickname(String nickname) {
        return userMapper.findByNickname(nickname) != null;
    }

    public Optional<User> findByEmail(String email) {
        return userMapper.findByEmail(email);
    }

    // 비밀번호 재설정
    public void updatePassword(String email, String newPassword) {
        String encodedPassword = passwordEncoder.encode(newPassword);
        userMapper.updatePasswordByEmail(email, encodedPassword);
    }

    public String getUserNicknameByUserId(Long userId) {
        return userMapper.getUserNicknameById(userId);
    }


}
package com.geonpil.service.user;

import com.geonpil.domain.user.User;
import com.geonpil.dto.user.PasswordChangeRequestDto;
import com.geonpil.mapper.user.UserMapper;
import com.geonpil.security.AppUserInfo;
import com.geonpil.security.CustomOAuth2User;
import com.geonpil.security.CustomUserDetails;
import com.geonpil.validator.NicknameValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
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



    // 유저닉네임 가져오기
    public String getUserNicknameByUserId(Long userId) {
        return userMapper.getUserNicknameById(userId);
    }




    public User findUserById(Long userId) {
        Optional<User> userOptional = userMapper.findUserById(userId);
        return userOptional.orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));
    }

    public void changePassword(Long userId, PasswordChangeRequestDto request) {
        User user = userMapper.findUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));


        System.out.println("입력한 현재 비밀번호: " + request.getCurrentPassword());
        System.out.println("DB 저장된 해시 비밀번호: " + user.getPassword());

        boolean matched = passwordEncoder.matches(request.getCurrentPassword(), user.getPassword());

        System.out.println("일치 여부: " + matched);

        if (!matched) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않아요.");
        }

        if(!request.getNewPassword().equals(request.getConfirmPassword())){
            throw new IllegalArgumentException("확인 비밀번호가 일치하지 않아요.");
        }

        if (user.getProvider() != null) {
            throw new IllegalStateException("소셜 로그인 사용자(예: 네이버, 카카오)는 비밀번호를 변경할 수 없습니다.<br>\n" +
                    "계정 관련 설정은 해당 로그인 제공 사이트에서 변경해주세요.");
        }

        String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());
        userMapper.updatePassword(userId, encodedNewPassword);
    }


    public void changeNickname(Long userId, String newNickname) {
        if (!NicknameValidator.isValid(newNickname)) {
            throw new IllegalArgumentException("닉네임 형식이 올바르지 않습니다.");
        }

        if (existsByNickname(newNickname)) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        userMapper.updateNickname(userId, newNickname);

        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
        AppUserInfo userInfo = (AppUserInfo) currentAuth.getPrincipal();

        User updatedUser = userMapper.findUserById(userInfo.getId())
                .orElseThrow(() -> new UsernameNotFoundException("사용자 없음"));

        Authentication newAuth;

        if (userInfo instanceof CustomOAuth2User) {
            CustomOAuth2User oAuthUser = (CustomOAuth2User) userInfo;

            newAuth = new UsernamePasswordAuthenticationToken(
                    new CustomOAuth2User(
                            Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                            oAuthUser.getAttributes(),  // 기존 소셜 정보 유지
                            "id",
                            updatedUser.getNickname(),
                            updatedUser.getId()
                    ),
                    currentAuth.getCredentials(),
                    Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
            );

        } else {
            newAuth = new UsernamePasswordAuthenticationToken(
                    new CustomUserDetails(updatedUser),
                    currentAuth.getCredentials(),
                    Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
            );
        }

        SecurityContextHolder.getContext().setAuthentication(newAuth);


    }
}
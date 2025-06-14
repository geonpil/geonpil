package com.geonpil.security;

import com.geonpil.domain.user.User;
import com.geonpil.mapper.user.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("email:" + email);

        User user = userMapper.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다." + email));

        if (user.isDeleted()) {
            throw new DisabledException("탈퇴한 계정입니다.");
        }

        List<String> roleNames = userMapper.findRolesById(user.getId());

        System.out.println("userId:" + user.getId());
        System.out.println("roleNames:" + roleNames);
        user.setRoles(roleNames);

        return new CustomUserDetails(user, roleNames);
    }
}

package com.geonpil.controller.user;

import com.geonpil.dto.user.PasswordChangeRequestDto;
import com.geonpil.security.AppUserInfo;
import com.geonpil.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserRestController {

    private final UserService userService;



/*    @PutMapping("/me")
    public ResponseEntity<?> updateMyInfo(@AuthenticationPrincipal AppUserInfo userInfo,
                                          @RequestBody UserUpdateRequest request) {
        userService.updateUser(userInfo.getId(), request);
        return ResponseEntity.ok("회원 정보가 수정되었습니다.");
    }*/

    @PutMapping("/password")
    public ResponseEntity<?> changePassword(@AuthenticationPrincipal AppUserInfo userInfo,
                                            @RequestBody PasswordChangeRequestDto request) {
        userService.changePassword(userInfo.getId(), request);
        return ResponseEntity.ok(Map.of("success", true, "message", "비밀번호가 변경되었습니다. 다시 로그인 해주세요!"));
    }

}

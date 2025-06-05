package com.geonpil.controller.user;

import com.geonpil.domain.user.User;
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




    @PutMapping("/nickname")
    public ResponseEntity<?> updateNickname(@AuthenticationPrincipal AppUserInfo userInfo,
                                            @RequestBody Map<String, String> payload) {
        String nickname = payload.get("nickname");


        try {
            userService.changeNickname(userInfo.getId(), nickname);
            return ResponseEntity.ok(Map.of("success", true, "message", "닉네임이 변경되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }



    @GetMapping("/check-nickname")
    public ResponseEntity<?> checkNickname(@RequestParam String nickname) {
        boolean exists = userService.existsByNickname(nickname);
        return ResponseEntity.ok(Map.of("exists", exists));
    }




}

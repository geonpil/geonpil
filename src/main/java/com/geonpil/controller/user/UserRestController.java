package com.geonpil.controller.user;

import com.geonpil.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
    }

    @PatchMapping("/me/password")
    public ResponseEntity<?> changePassword(@AuthenticationPrincipal AppUserInfo userInfo,
                                            @RequestBody PasswordChangeRequest request) {
        userService.changePassword(userInfo.getId(), request);
        return ResponseEntity.ok("비밀번호가 변경되었습니다.");
    }

    @DeleteMapping("/me")
    public ResponseEntity<?> deleteMyAccount(@AuthenticationPrincipal AppUserInfo userInfo) {
        userService.deleteUser(userInfo.getId());
        return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
    }*/
}

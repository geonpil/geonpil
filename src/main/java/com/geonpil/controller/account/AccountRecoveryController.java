package com.geonpil.controller.account;

import com.geonpil.mapper.user.UserMapper;
import com.geonpil.mapper.user.UserStatusHistoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequiredArgsConstructor
public class AccountRecoveryController {

    private final UserMapper userMapper;
    private final UserStatusHistoryMapper historyMapper;

    // 확인 팝업 페이지
    @GetMapping("/recover")
    public String recoverConfirmPage(@RequestParam("userId") Long userId, Model model) {
        model.addAttribute("userId", userId);
        return "account/recover"; // templates/account/recover.html
    }

    // 사용자가 복구에 동의
    @PostMapping("/recover/confirm")
    public String recoverConfirm(@RequestParam("userId") Long userId) {
        userMapper.updateForRecover(new com.geonpil.domain.user.User() {{ setId(userId); }});
        historyMapper.insertStatusHistory(userId, "RECOVER");
        String msg = URLEncoder.encode("계정이 복구되었습니다. 다시 로그인해주세요.", StandardCharsets.UTF_8);
        return "redirect:/login?message=" + msg;
    }

} 
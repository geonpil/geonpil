package com.geonpil.service.user;

import com.geonpil.domain.withdrawal.WithdrawalReason;
import com.geonpil.mapper.user.UserMapper;
import com.geonpil.mapper.withdrawalReason.WithdrawalReasonMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WithdrawlService {

    private final UserMapper userMapper;
    private final WithdrawalReasonMapper withdrawalReasonMapper;

    @Transactional
    public void process(Long userId, List<String> reasons, String feedback) {
        // 1. 회원 탈퇴 처리 (소프트 삭제 or 실제 삭제)
        userMapper.softDeleteUserId(userId); // is_deleted = true


        String reasonString = String.join(",", reasons);

        // 2. 탈퇴 사유 저장
        withdrawalReasonMapper.insertWithdrawalReasons(new WithdrawalReason(userId, reasonString, feedback));
    }

}
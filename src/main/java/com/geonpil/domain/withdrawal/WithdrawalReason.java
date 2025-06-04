package com.geonpil.domain.withdrawal;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class WithdrawalReason {
    private Long userId;
    private String reasons;   // CSV 형태로 저장
    private String feedback;
}






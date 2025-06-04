package com.geonpil.mapper.withdrawalReason;

import com.geonpil.domain.withdrawal.WithdrawalReason;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WithdrawalReasonMapper {
    void insertWithdrawalReasons(WithdrawalReason withdrawalReason);
}
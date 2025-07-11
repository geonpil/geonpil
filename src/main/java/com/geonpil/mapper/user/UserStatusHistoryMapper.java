package com.geonpil.mapper.user;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserStatusHistoryMapper {
    void insertStatusHistory(@Param("userId") Long userId, @Param("action") String action);
} 
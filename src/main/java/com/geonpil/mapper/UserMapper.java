package com.geonpil.mapper;

import com.geonpil.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

@Mapper
public interface UserMapper {
    void insert(User user);


    @Select("SELECT COUNT(*) > 0 FROM users WHERE email = #{email}")
    boolean existsByEmail(String email);


    Optional<User> findByEmail(String email);

    User findByNickname(String nickname);

    void updatePasswordByEmail(@Param("email") String email, @Param("password") String password);

}
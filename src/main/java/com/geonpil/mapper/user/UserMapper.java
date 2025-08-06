package com.geonpil.mapper.user;

import com.geonpil.domain.user.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserMapper {
    void insertLocalUser(User user);

    void insertSocialUser(User user);




    @Select("SELECT COUNT(*) > 0 FROM users WHERE email = #{email}")
    boolean existsByEmail(String email);

    @Select("SELECT COUNT(*) > 0 FROM users WHERE nickname = #{nickname}")
    boolean existsByNickname(String nickname);


    Optional<User> findByEmail(String email);

    Optional<User> findByProviderAndProviderId(@Param("provider") String provider,
                                               @Param("providerId") String providerId
                                               );

    User findByNickname(String nickname);

    void updatePasswordByEmail(@Param("email") String email, @Param("password") String password);

    String getUserNicknameById(long id);

    Optional<User> findUserById(long id);

    void softDeleteUserId(@Param("id") Long id);

    void updatePassword(@Param("userId") Long id, @Param("newPassword") String encodedPassword);

    void updateNickname(@Param("userId") Long id, @Param("newNickname") String newNickname);

    List<String> findRolesById(long Id);

    void updateForRecover(User user);
}
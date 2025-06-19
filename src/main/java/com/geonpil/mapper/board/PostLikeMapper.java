package com.geonpil.mapper.board;

import com.geonpil.domain.PostLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PostLikeMapper {

    int countByPostIdAndUserId(@Param("postId") Long postId, @Param("userId") Long userId);

    void insert(PostLike postLike);

    void incrementLikeCount(@Param("postId") Long postId);
}

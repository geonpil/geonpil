package com.geonpil.mapper.review;

import com.geonpil.domain.Review;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReviewLikeMapper {
    boolean existsByReviewIdAndUserId(@Param("reviewId") Long reviewId, @Param("userId") Long userId);

    void insertLike(@Param("reviewId") Long reviewId, @Param("userId") Long userId);

    void deleteLike(@Param("reviewId") Long reviewId, @Param("userId") Long userId);

    int countByReviewId(@Param("reviewId") Long reviewId);
}
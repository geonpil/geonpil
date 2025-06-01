package com.geonpil.mapper.review;

import com.geonpil.domain.Review;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ReviewMapper {
    List<Review> findByBookId(Long bookId);
    List<Review> getReviewsSortedByLikes(Long userId);
    void insertReview(Review review);
    void softDeleteById(Long id);

    void increaseLikeCount(Long reviewId);
    void decreaseLikeCount(Long reviewId);

    boolean existsByUserIdAndBookId(Long userId, Long bookId);


    Double getAverageRatingByBookId(Long bookId);


}
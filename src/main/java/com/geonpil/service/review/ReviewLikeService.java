package com.geonpil.service.review;


import com.geonpil.mapper.review.ReviewLikeMapper;
import com.geonpil.mapper.review.ReviewMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewLikeService {

    private final ReviewLikeMapper reviewLikeMapper;
    private final ReviewMapper reviewMapper;

    public int toggleLike(Long reviewId, Long userId) {
        boolean alreadyLiked = reviewLikeMapper.existsByReviewIdAndUserId(reviewId, userId);

        if (alreadyLiked) {
            reviewLikeMapper.deleteLike(reviewId, userId);
            reviewMapper.decreaseLikeCount(reviewId);
        } else {
            reviewLikeMapper.insertLike(reviewId, userId);
            reviewMapper.increaseLikeCount(reviewId);
        }

        return reviewLikeMapper.countByReviewId(reviewId);
    }

}

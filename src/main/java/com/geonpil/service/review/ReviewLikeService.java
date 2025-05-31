package com.geonpil.service.review;


import com.geonpil.mapper.review.ReviewLikeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewLikeService {

    private final ReviewLikeMapper reviewLikeMapper;

    public int toggleLike(Long reviewId, Long userId) {
        boolean alreadyLiked = reviewLikeMapper.existsByReviewIdAndUserId(reviewId, userId);

        if (alreadyLiked) {
            reviewLikeMapper.deleteLike(reviewId, userId);
        } else {
            reviewLikeMapper.insertLike(reviewId, userId);
        }

        return reviewLikeMapper.countByReviewId(reviewId);
    }

}

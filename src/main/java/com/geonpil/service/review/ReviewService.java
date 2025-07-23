package com.geonpil.service.review;

import com.geonpil.domain.Review;
import com.geonpil.mapper.review.ReviewMapper;
import com.geonpil.security.AppUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewMapper reviewMapper;

    public List<Review> getReviewsByBookId(Long bookId) {
        return reviewMapper.findByBookId(bookId);

    }

    public List<Review> getReviewsByBookId(Long bookId, String SortType) {
        return switch (SortType) {
            case "likes" -> reviewMapper.getReviewsSortedByLikes(bookId);
            case "recent" -> reviewMapper.findByBookId(bookId);
            default -> throw new IllegalArgumentException("Invalid sort type");
        };
    }

    public void addReview(Review review,  @AuthenticationPrincipal AppUserInfo user) {
        if (hasUserReviewedBook(user.getId(), review.getBookId())) {
            throw new IllegalStateException("이미 리뷰를 작성하셨습니다.");
        }

        review.setUserId(user.getId());
        reviewMapper.insertReview(review);
    }

    public void deleteReview(Long reviewId) {
        reviewMapper.softDeleteById(reviewId);
    }


    public double getAverageRating(Long bookId) {
        Double avgRating = reviewMapper.getAverageRatingByBookId(bookId);
        if (avgRating == null) {
            return 0.0;
        }
        return Math.round(avgRating * 100.0) / 100.0;
    }



    public boolean hasUserReviewedBook(Long userId, Long bookId) {
        return reviewMapper.existsByUserIdAndBookId(userId, bookId);
    }


}

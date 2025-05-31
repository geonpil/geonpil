package com.geonpil.service.review;

import com.geonpil.domain.Review;
import com.geonpil.mapper.review.ReviewMapper;
import com.geonpil.security.AppUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewMapper reviewMapper;


    public List<Review> getReviewsByBookId(Long bookId) {
        return reviewMapper.findByBookId(bookId);
    }

    public void addReview(Review review,  @AuthenticationPrincipal AppUserInfo user) {
        review.setUserId(user.getId());
        reviewMapper.insertReview(review);
    }

    public void deleteReview(Long reviewId) {
        reviewMapper.softDeleteById(reviewId);
    }


    public double calculateAverageRating(List<Review> reviews) {
        return reviews.isEmpty()
                ? 0.0
                : Math.round(
                reviews.stream()
                        .mapToDouble(Review::getRating)
                        .average()
                        .orElse(0.0) * 100.0
        ) / 100.0;
    }
}

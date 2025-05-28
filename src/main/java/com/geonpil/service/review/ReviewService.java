package com.geonpil.service.review;

import com.geonpil.domain.Review;
import com.geonpil.mapper.review.ReviewMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewMapper reviewMapper;

    // 1. 리뷰 등록
    public void writeReview(Review review) {
        reviewMapper.insert(review);
    }

    // 2. 특정 책에 대한 리뷰 조회
    public List<Review> getReviewsByBookId(Long bookId) {
        return reviewMapper.findByBookId(bookId);
    }

    // 3. 리뷰 상세 조회
    public Review getReviewById(Long reviewId) {
        return reviewMapper.findById(reviewId);
    }

    // 4. 리뷰 수정
    public void updateReview(Review review) {
        reviewMapper.update(review);
    }

    // 5. 리뷰 삭제 (soft delete)
    public void deleteReview(Long reviewId) {
        reviewMapper.softDelete(reviewId);
    }
}
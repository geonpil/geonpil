package com.geonpil.controller.review;

import ch.qos.logback.core.model.Model;
import com.geonpil.domain.Review;
import com.geonpil.dto.review.ReviewRequestDto;
import com.geonpil.dto.review.ReviewResponseDto;
import com.geonpil.security.AppUserInfo;
import com.geonpil.service.review.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    // 리뷰 등록
    @PostMapping
    @ResponseBody
    public ResponseEntity<?> addReview(@RequestBody ReviewRequestDto dto
                                    , @AuthenticationPrincipal AppUserInfo user) {
        Review review = new Review();
        review.setBookId(dto.getBookId());
        review.setRating(dto.getRating());
        review.setContent(dto.getContent());
        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());

        reviewService.addReview(review, user);


        ReviewResponseDto responseDto = new ReviewResponseDto(
                review.getBookId(),
                user.getNickname(), // 또는 username, userId 등
                review.getRating(),
                review.getContent(),
                review.getCreatedAt()
        );

        // Location 헤더에 등록된 책 상세 URI 반환
        return ResponseEntity.ok(responseDto);
    }

    // 특정 책의 모든 리뷰 조회 (선택적)
    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<Review>> getReviews(@PathVariable Long bookId) {
        List<Review> reviews = reviewService.getReviewsByBookId(bookId);
        return ResponseEntity.ok(reviews);
    }

    // 리뷰 삭제
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}

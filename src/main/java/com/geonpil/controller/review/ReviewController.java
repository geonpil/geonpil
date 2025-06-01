package com.geonpil.controller.review;

import org.springframework.ui.Model;
import com.geonpil.domain.Review;
import com.geonpil.dto.review.ReviewRequestDto;
import com.geonpil.dto.review.ReviewResponseDto;
import com.geonpil.security.AppUserInfo;
import com.geonpil.service.review.ReviewLikeService;
import com.geonpil.service.review.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    private final ReviewLikeService reviewLikeService;

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


        try {
            reviewService.addReview(review, user);
        } catch ( IllegalStateException e ) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }




        ReviewResponseDto responseDto = ReviewResponseDto.builder()
                                        .userId(review.getUserId())
                                        .reviewId(review.getReviewId())
                                        .bookId(review.getBookId())
                                        .username(user.getNickname())
                                        .rating(review.getRating())
                                        .content(review.getContent())
                                        .createdAt(review.getCreatedAt()).build();



        // Location 헤더에 등록된 책 상세 URI 반환
        return ResponseEntity.ok(responseDto);
    }


    // 리뷰 삭제
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build(); // 204 No Content
    }


    // 리뷰 평균 가져오기
    @GetMapping("/average")
    @ResponseBody
    public double getAverageRating(@RequestParam Long bookId) {
        return reviewService.getAverageRating(bookId);
    }

    //리뷰 좋아요
    @PostMapping("/{reviewId}/like")
    @ResponseBody
    public ResponseEntity<Integer> toggleLike(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal AppUserInfo user) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        int updatedLikeCount = reviewLikeService.toggleLike(reviewId, user.getId());
        return ResponseEntity.ok(updatedLikeCount);
    }




}

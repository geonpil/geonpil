package com.geonpil.util.mapper;

import com.geonpil.domain.Review;
import com.geonpil.dto.review.ReviewResponseDto;
import com.geonpil.mapper.review.ReviewLikeMapper;
import com.geonpil.security.AppUserInfo;
import com.geonpil.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ReviewMapperUtil {

    private final UserService userService;
    private final ReviewLikeMapper reviewLikeMapper;

    public List<ReviewResponseDto> convertToResponseDto(List<Review> reviews, AppUserInfo user) {
        return reviews.stream()
                .map(review -> {
                    boolean liked = false;
                    if (user != null) {
                        liked = reviewLikeMapper.existsByReviewIdAndUserId(review.getReviewId(), user.getId());
                    }

                    return ReviewResponseDto.builder()
                            .reviewId(review.getReviewId())
                            .userId(review.getUserId())
                            .bookId(review.getBookId())
                            .username(userService.getUserNicknameByUserId(review.getUserId()))
                            .rating(review.getRating())
                            .content(review.getContent())
                            .createdAt(review.getCreatedAt())
                            .likedByCurrentUser(liked)
                            .likeCount(reviewLikeMapper.countByReviewId(review.getReviewId()))
                            .build();
                })
                .collect(Collectors.toList());
    }
}
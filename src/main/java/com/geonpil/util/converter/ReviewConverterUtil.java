package com.geonpil.util.converter;

import com.geonpil.domain.Review;
import com.geonpil.domain.ReviewComment;
import com.geonpil.dto.review.ReviewCommentDto;
import com.geonpil.dto.review.ReviewResponseDto;
import com.geonpil.mapper.review.ReviewLikeMapper;
import com.geonpil.security.AppUserInfo;
import com.geonpil.service.user.UserService;
import com.geonpil.service.review.ReviewCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ReviewConverterUtil {

    private final UserService userService;
    private final ReviewLikeMapper reviewLikeMapper;
    private final ReviewCommentService reviewCommentService;

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
                            .isDeleted(review.getIsDeleted())
                            .build();
                })
                .collect(Collectors.toList());
    }


    public void attachCommentsToReviews(List<ReviewResponseDto> reviewDtos) {
        for (ReviewResponseDto reviewDto : reviewDtos) {
            List<ReviewComment> comments = reviewCommentService.getCommentsByReviewId(reviewDto.getReviewId());
            List<ReviewCommentDto> commentDtos = comments.stream()
                    .map(comment -> ReviewCommentDto.builder()
                            .reviewCommentId(comment.getReviewCommentId())
                            .reviewId(comment.getReviewId())
                            .parentId(comment.getParentId())
                            .userId(comment.getUserId())
                            .username(userService.getUserNicknameByUserId(comment.getUserId()))
                            .content(comment.getContent())
                            .createdAt(comment.getCreatedAt())
                            .build())
                    .collect(Collectors.toList());
            reviewDto.setReviewCommentDtos(commentDtos);
        }
    }


    public List<ReviewResponseDto> filterVisibleReviewsWithComments(List<ReviewResponseDto> reviewDtos) {
        return reviewDtos.stream()
                .filter(review ->
                        !review.isDeleted() ||
                                (review.getReviewCommentDtos() != null && !review.getReviewCommentDtos().isEmpty())
                )
                .collect(Collectors.toList());
    }


}
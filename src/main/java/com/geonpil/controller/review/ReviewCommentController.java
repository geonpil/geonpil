package com.geonpil.controller.review;

import com.geonpil.domain.Review;
import com.geonpil.domain.ReviewComment;
import com.geonpil.dto.review.ReviewCommentDto;
import com.geonpil.dto.review.ReviewResponseDto;
import com.geonpil.mapper.review.ReviewLikeMapper;
import com.geonpil.security.AppUserInfo;
import com.geonpil.service.UserService;
import com.geonpil.service.review.ReviewCommentService;
import com.geonpil.service.review.ReviewLikeService;
import com.geonpil.service.review.ReviewService;
import com.geonpil.util.mapper.ReviewMapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewCommentController {


    private final ReviewCommentService ReviewCommentService;


    //리뷰 댓글 달기
    @PostMapping("/comments")
    @ResponseBody
    public ResponseEntity<?> addComment( @RequestBody ReviewCommentDto dto,
                                        @AuthenticationPrincipal AppUserInfo user) {
        ReviewComment reviewComment = new ReviewComment();
        reviewComment.setReviewId(dto.getReviewId());
        reviewComment.setParentId(dto.getParentId());
        reviewComment.setUserId(user.getId());
        reviewComment.setContent(dto.getContent());
        reviewComment.setCreatedAt(LocalDateTime.now());
        ReviewCommentService.save(reviewComment);


        // 응답용 DTO 생성
        ReviewCommentDto responseDto = ReviewCommentDto.builder()
                .reviewCommentId(reviewComment.getReviewCommentId())  // 저장 후 ID
                .reviewId(reviewComment.getReviewId())
                .parentId(reviewComment.getParentId())
                .userId(user.getId())
                .username(user.getNickname())
                .content(reviewComment.getContent())
                .createdAt(reviewComment.getCreatedAt())
                .build();

        return ResponseEntity.ok(responseDto);
    }



}

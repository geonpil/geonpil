package com.geonpil.controller.review;

import com.geonpil.domain.ReviewComment;
import com.geonpil.dto.review.ReviewCommentDto;
import com.geonpil.security.AppUserInfo;
import com.geonpil.service.review.ReviewCommentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewCommentController {


    private final ReviewCommentService reviewCommentService;


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
        reviewCommentService.save(reviewComment);


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

    //리뷰 댓글 삭제
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<?> deleteReviewComment(@PathVariable Long commentId,
                                                 @AuthenticationPrincipal AppUserInfo userInfo) {
        try {
            reviewCommentService.deleteComment(commentId, userInfo.getId());
            return ResponseEntity.ok("댓글이 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("삭제 권한이 없습니다.");
        } catch (Exception e) {
            log.error("댓글 삭제 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 삭제 중 오류 발생");
        }
    }


}

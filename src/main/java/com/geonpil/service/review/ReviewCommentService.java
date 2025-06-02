package com.geonpil.service.review;


import com.geonpil.domain.ReviewComment;
import com.geonpil.dto.review.ReviewCommentDto;
import com.geonpil.mapper.review.ReviewCommentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewCommentService {
    private final ReviewCommentMapper reviewCommentMapper;

    public List<ReviewComment> getCommentsByReviewId(Long reviewId) {
        return reviewCommentMapper.findReviewCommentByReviewId(reviewId);
    }

    public void save(ReviewComment comment) {
        reviewCommentMapper.insertReviewComment(comment);
    }

    public void deleteComment(Long commentId, Long userId) {
        ReviewComment comment = reviewCommentMapper.findReviewCommentById(commentId);
        if (comment == null || !comment.getUserId().equals(userId)) {
            throw new IllegalArgumentException("권한 없음");
        }
        reviewCommentMapper.softDeleteReviewComment(commentId);
    }
}

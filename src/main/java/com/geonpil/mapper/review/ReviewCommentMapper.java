package com.geonpil.mapper.review;

import com.geonpil.domain.Comment;
import com.geonpil.domain.ReviewComment;
import com.geonpil.dto.review.ReviewCommentDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReviewCommentMapper {
    List<ReviewComment> findReviewCommentByReviewId(Long reviewId);
    void insertReviewComment(ReviewComment comment);
    void softDeleteReviewComment(Long reivewCommentId);
    ReviewComment findReviewCommentById(Long commentId);
}

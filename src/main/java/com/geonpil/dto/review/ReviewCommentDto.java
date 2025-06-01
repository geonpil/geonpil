package com.geonpil.dto.review;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@Builder
public class ReviewCommentDto {
    private Long reviewCommentId;
    private Long reviewId;
    private Long parentId; // null이면 원댓글, 값이 있으면 대댓글
    private Long userId;
    private String username;
    private String content;
    private LocalDateTime createdAt;
}


package com.geonpil.domain;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReviewComment {
    private Long reviewCommentId;
    private Long reviewId;
    private Long parentId; // 원댓글이면 null
    private Long userId;
    private String content;
    private LocalDateTime createdAt;
    private Boolean isDeleted;
}
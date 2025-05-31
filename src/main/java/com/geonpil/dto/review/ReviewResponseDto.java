package com.geonpil.dto.review;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReviewResponseDto {
    private Long reviewId;
    private Long userId;
    private Long bookId;
    private String username; // 또는 userId 등
    private double rating;
    private String content;
    private LocalDateTime createdAt;
    private int likeCount;
    private boolean likedByCurrentUser;

}

package com.geonpil.dto.review;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ReviewRequestDto {
    private Long bookId;
    private double rating;
    private String content;
}

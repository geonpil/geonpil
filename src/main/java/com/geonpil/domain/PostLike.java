package com.geonpil.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostLike {
    private Long likeId;
    private Long postId;
    private Long userId;
    private Date createdAt;
}

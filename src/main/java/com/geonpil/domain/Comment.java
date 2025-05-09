package com.geonpil.domain;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class Comment {
    private Long commentId;
    private Long postId;
    private Long userId;
    private String nickname;
    private String content;
    private Boolean isDeleted;
    private Date  createdAt;
    private Date  updatedAt;
    private Long parentId;
    private int replyCount;
    private boolean hasReplies;

    public boolean isHasReplies() {
        return replyCount > 0;
    }
}

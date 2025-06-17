package com.geonpil.domain;

import com.geonpil.elasticsearch.BoardDocument;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.sql.Timestamp;

@Data
public class BoardDTO {
    @Id
    private Long postId;
    private Long userId;
    private int boardCode;
    private Long categoryId;
    private String categoryName;
    private String nickname;
    private String title;
    private String content;
    private int viewCount;
    private int likeCount;
    private int commentCount; // ✅ 댓글 수
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public static BoardDTO from(BoardDocument doc) {
        BoardDTO dto = new BoardDTO();
        dto.setPostId(doc.getPostId());
        dto.setTitle(doc.getTitle());
        dto.setContent(doc.getContent());
        return dto;
    }




}




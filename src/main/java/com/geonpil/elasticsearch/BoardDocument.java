package com.geonpil.elasticsearch;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import java.sql.Timestamp;

@Data
@Document(indexName = "board")  // Elasticsearch 인덱스명
public class BoardDocument {

    @Id
    private Long postId;

    private Long userId;
    private int boardCode;
    private Long categoryId;
    private String title;
    private String content;
    private int viewCount;
    private int likeCount;
    private Timestamp createdAt;
}
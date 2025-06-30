package com.geonpil.elasticsearch;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import java.sql.Timestamp;

@Data
@Document(indexName = "board")  // Elasticsearch 인덱스명
@AllArgsConstructor   // 모든 필드를 받는 생성자
@NoArgsConstructor
public class BoardDocument {
    @Id
    private Long postId;

    @Field(type = FieldType.Text, analyzer = "standard", searchAnalyzer = "standard")
    private String title;

    @Field(type = FieldType.Text, analyzer = "standard", searchAnalyzer = "standard")
    private String content;
    
    @Field(type = FieldType.Integer)
    private int boardCode;

    @Field(type = FieldType.Long)
    private Long categoryId;

}



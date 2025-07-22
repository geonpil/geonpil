package com.geonpil.elasticsearch;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;
import java.util.List;

@Data
@Document(indexName = "contest")  // Elasticsearch 인덱스명
@AllArgsConstructor   // 모든 필드를 받는 생성자
@NoArgsConstructor
public class ContestDocument {
    @Id
    private Long postId;

    @Field(type = FieldType.Text, analyzer = "standard", searchAnalyzer = "standard")
    private String title;

    @Field(type = FieldType.Text, analyzer = "standard", searchAnalyzer = "standard")
    private String content;

    @Field(type = FieldType.Integer)
    private int boardCode;

    @Field(type = FieldType.Long)
    private List<Long> categoryIds;

    // ContestDocument 고유 필드
    @Field(type = FieldType.Text, analyzer = "standard", searchAnalyzer = "standard")
    private String subtitle;

    @Field(type = FieldType.Text, analyzer = "standard", searchAnalyzer = "standard")
    private String hostName;

    @Field(type = FieldType.Text, analyzer = "standard", searchAnalyzer = "standard")
    private String target;

    @Field(type = FieldType.Date, format = DateFormat.date)
    private LocalDate startDate;

    @Field(type = FieldType.Date, format = DateFormat.date)
    private LocalDate endDate;

}
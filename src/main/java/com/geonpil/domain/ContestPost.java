package com.geonpil.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ContestPost extends BoardDTO {
    private String subtitle;           // 부제목 (요약 설명)
    private String posterUrl;          // 포스터 이미지 경로
    private LocalDate startDate;       // 시작일
    private LocalDate endDate;         // 마감일
    private String hostName;           // 주최사
    private String target;             // 응모 대상자
    private String totalPrize;         // 총 상금
    private String firstPrize;         // 1등 상금
    private String tags;               // 태그 문자열 (예: "문학,디자인")
    private String pastWinnerInfo;     // 수상작 정보
    private int dDay;                  // 남은 일수 (계산용, DB 저장 X)
    private String applyUrl;           // 지원 링크
    private String contactInfo;           // 문의 주소
    private List<Long> categoryIds; // 카테고리들
    private String categoryIdsRaw;
    private String categoryNames; // 카테고리들


    public List<Long> getCategoryIdsFromRaw() {
        if (categoryIdsRaw == null || categoryIdsRaw.isBlank()) return Collections.emptyList();
        return Arrays.stream(categoryIdsRaw.split(","))
                .map(String::trim)
                .map(Long::valueOf)
                .collect(Collectors.toList());
    }

}

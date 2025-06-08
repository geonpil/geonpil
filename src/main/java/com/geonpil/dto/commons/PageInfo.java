package com.geonpil.dto.commons;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageInfo {
    private int currentPage;   // 현재 페이지
    private int startPage;     // 시작 페이지 (ex: 1, 11, 21)
    private int endPage;       // 끝 페이지
    private int totalPages;    // 전체 페이지 수
    private boolean hasPrevGroup; // ◀ 표시 여부
    private boolean hasNextGroup; // ▶ 표시 여부
    private String query;      // 검색어 또는 기타 파라미터 (optional)
}

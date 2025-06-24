package com.geonpil.util;

import com.geonpil.dto.commons.PageInfo;

public class PaginationUtil {

    public static PageInfo buildPageInfo(int currentPage, int totalPages, int pageGroupSize, String query) {
        int startPage = ((currentPage - 1) / pageGroupSize) * pageGroupSize + 1;
        int endPage = Math.max(1, Math.min(startPage + pageGroupSize - 1, totalPages));

        boolean hasPrevGroup = startPage > 1;
        boolean hasNextGroup = endPage < totalPages;


        System.out.println("디버그 : startPage = " + startPage + ", endPage = " + endPage + ", totalPages = " + totalPages);
        return PageInfo.builder()
                .currentPage(currentPage)
                .startPage(startPage)
                .endPage(endPage)
                .totalPages(totalPages)
                .hasPrevGroup(hasPrevGroup)
                .hasNextGroup(hasNextGroup)
                .query(query)
                .build();
    }
}
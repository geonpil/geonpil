package com.geonpil.domain;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class PageResult<T> {
    private List<T> posts;
    private int page;
    private int totalPages;

    public PageResult(List<T> posts, int page, int totalPages) {
        this.posts = posts;
        this.page = page;
        this.totalPages = totalPages;
    }

}
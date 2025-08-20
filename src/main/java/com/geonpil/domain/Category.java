package com.geonpil.domain;


import lombok.Data;

@Data
public class Category {
    private Long id;
    private String categoryName; // 👍 명확
    private Integer boardCode;
    private Integer isDeleted;
}

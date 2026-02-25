package com.geonpil.dto.bookSearch;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PopularKeyword {
    private String keyword;
    private Long count;
}

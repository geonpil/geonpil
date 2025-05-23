package com.geonpil.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Book {
    private String title;
    private String contents;
    private List<String> authors;
    private String publisher;
    private String thumbnail;
    private String url;
}

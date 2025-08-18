package com.geonpil.domain.board;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class BoardType {
    private Long boardCode;
    private String boardName;
    private String boardDescription;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}





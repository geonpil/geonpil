package com.geonpil.domain.bugReport;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BugReport {
    private Long id;               // optional: 조회 시 사용
    private String nickname;       // 작성자 별명
    private String content;        // 버그 내용
    private String ipAddress;      // IP 주소
    private LocalDateTime createdAt; // 작성 시각
}

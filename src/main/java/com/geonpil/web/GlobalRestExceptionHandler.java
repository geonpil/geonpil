package com.geonpil.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalRestExceptionHandler {

    @ExceptionHandler(MailSendFailedException.class)
    public ResponseEntity<String> handleMailSendFailed(MailSendFailedException e) {
        // 프론트에서 res.ok 체크 후 이 메시지를 그대로 안내문구로 사용
        return ResponseEntity.internalServerError().body(e.getMessage());
    }
}


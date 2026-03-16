package com.geonpil.web;

public class MailSendFailedException extends RuntimeException {
    public MailSendFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}


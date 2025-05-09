package com.geonpil.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String text) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setFrom("lwd33021@naver.com");
            helper.setSubject(subject);
            helper.setText(text, true); // true -> HTML 가능

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }


    // 인증코드를 생성해서 이메일로 보내고, 인증코드를 리턴하는 메소드
    public String sendVerificationCode(String to) {
        String code = String.valueOf((int)((Math.random() * 900000) + 100000)); // 6자리 숫자

        String subject = "[건필] 비밀번호 찾기 인증코드 안내";
        String content = "<h3>인증코드: " + code + "</h3>";

        sendEmail(to, subject, content); // 기존 sendEmail 메소드 활용해서 메일 전송

        return code; // 세션에 저장하기 위해 리턴
    }


}

package com.geonpil.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import jakarta.mail.internet.MimeMessage;

@Service
public class MailService {

    private final JavaMailSender mailSender;
    private final String mailFrom;

    public MailService(JavaMailSender mailSender, @Value("${spring.mail.username:}") String mailFrom) {
        this.mailSender = mailSender;
        this.mailFrom = mailFrom;
    }

    public void sendEmail(String to, String subject, String text) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setFrom((mailFrom != null && !mailFrom.isBlank()) ? mailFrom : "no-reply@geonpil.local");
            helper.setSubject(subject);
            helper.setText(text, true); // true -> HTML 가능

            mailSender.send(message);
        } catch (Exception e) {
            // SMTP 인증/연결 오류 등은 컨트롤러에서 메시지로 내려줄 수 있게 런타임 예외로 래핑
            throw new com.geonpil.web.MailSendFailedException("메일 전송에 실패했습니다. 메일 계정/SMTP 설정을 확인해주세요.", e);
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


    // 회원가입 인증코드를 생성해서 이메일로 보내고, 인증코드를 리턴하는 메소드
    public String sendSignupVerificationCode(String to) {
        String code = String.valueOf((int) ((Math.random() * 900000) + 100000)); // 6자리 숫자

        String subject = "[건필] 이메일 인증 코드 안내";
        String content = """
            <div style=\"font-family: 'Apple SD Gothic Neo', Arial, sans-serif; background:#f5f6fa; padding:30px;\">
                <div style=\"max-width:600px;margin:0 auto;background:#ffffff;border-radius:8px;padding:40px;\">
                    <h2 style=\"color:#2d3436; margin-top:0;\">건필에 오신 것을 환영합니다! 🎉</h2>
                    <p style=\"font-size:16px; margin:20px 0;\">
                        아래 <strong>인증코드</strong>를 회원가입 페이지에 입력하여 이메일 인증을 완료해주세요.
                    </p>
                    <div style=\"font-size:32px;font-weight:bold;color:#0984e3;letter-spacing:4px; margin:30px 0; text-align:center;\">%s</div>
                    <p style=\"font-size:14px; color:#636e72;\">감사합니다.<br/>건필 팀 드림</p>
                </div>
            </div>
            """.formatted(code);

        sendEmail(to, subject, content);
        return code;
    }


}

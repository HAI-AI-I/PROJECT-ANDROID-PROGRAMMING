package com.group_7.library_management.service;

import com.group_7.library_management.exception.EmailDeliveryException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.time.Duration;

@Service
public class RegistrationEmailService {

    private final JavaMailSender mailSender;
    private final String senderAddress;
    private final Duration expiration;

    public RegistrationEmailService(
            JavaMailSender mailSender,
            @Value("${spring.mail.username:}") String senderAddress,
            @Value("${app.registration-otp.expiration:PT5M}") Duration expiration
    ) {
        this.mailSender = mailSender;
        this.senderAddress = senderAddress;
        this.expiration = expiration;
    }

    public void sendRegistrationCode(String recipient, String code) {
        if (senderAddress.isBlank()) {
            throw new EmailDeliveryException(
                    "Máy chủ chưa được cấu hình tài khoản Gmail để gửi mã xác nhận"
            );
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderAddress);
        message.setTo(recipient);
        message.setSubject("Mã xác nhận đăng ký Library Management");
        long minutes = expiration.toMinutes();
        message.setText("Mã xác nhận của bạn là: " + code
                + "\n\nMã có hiệu lực trong "+ minutes + " phút. Không cung cấp mã này cho người khác.");
        try {
            mailSender.send(message);
        } catch (MailException exception) {
            throw new EmailDeliveryException("Không thể gửi mã xác nhận tới email này");
        }
    }
}

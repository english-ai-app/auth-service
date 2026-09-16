package com.EnglishApp.auth_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {
    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    public void sendVerificationOtp(String email, String otp) {
        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();

        if (mailSender == null) {
            log.info("email_otp_dev_fallback email={} otp={}", email, otp);
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("English App verification code");
        message.setText("Your verification code is " + otp + ". It expires in 5 minutes.");
        mailSender.send(message);
    }
}

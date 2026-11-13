package com.emre.tournament.auth.service;

import org.springframework.lang.Nullable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Nullable
    private final JavaMailSender mailSender;

    public MailService(@Nullable JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendConfirmationEmail(String to, String token) {
        String link = "http://localhost:8080/api/auth/confirm?token=" + token;

        if (mailSender == null) {
            System.out.println("CONFIRM LINK for " + to + ": " + link);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Confirm your account");
            message.setText("Click to confirm your account:\n" + link);
            mailSender.send(message);
        } catch (Exception e) {
            System.out.println("CONFIRM LINK for " + to + ": " + link);
        }
    }

    public void sendPasswordResetEmail(String to, String token) {
        String link = "http://localhost:8080/api/auth/reset-password?token=" + token;

        if (mailSender == null) {
            System.out.println("RESET LINK for " + to + ": " + link);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Reset your password");
            message.setText("Click to reset your password:\n" + link);
            mailSender.send(message);
        } catch (Exception e) {
            System.out.println("RESET LINK for " + to + ": " + link);
        }
    }
}

package com.backend.fintech.application.core.services;

import com.backend.fintech.application.auth.models.ResetLink;
import com.backend.fintech.application.user.models.User;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final EmailTemplateService emailTemplateService;

    @Async("emailExecutor")
    public void sendVerificationEmail(String toEmail, String token) {
        try {

            // TODO: integrate JavaMailSender here
            System.out.println("Sending email to: " + toEmail);
            String messageBody = emailTemplateService.buildVerifyEmailTemplate(token);
            sendHtmlEmail(
                    toEmail,
                    "Verify Email",
                    messageBody
            );
            System.out.println("Verification code: " + token);

        } catch (Exception e) {
            // log error properly
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }

    @Async("emailExecutor")
    public void sendResetPassword(ResetLink resetLink) {
        try {
            // TODO: integrate JavaMailSender here
            System.out.println("Sending email to: " + resetLink.getUser().getEmail());
            String messageBody = emailTemplateService.buildResetPasswordTemplate(resetLink.getCode());
            sendHtmlEmail(
                    resetLink.getUser().getEmail(),
                    "Password Reset Request",
                    messageBody);
            System.out.println("Password reset link: " + resetLink.getLink());

        } catch (Exception e) {
            // log error properly
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }

    public void sendNotification(String title, String message, User user) {
        try {
            // TODO: integrate JavaMailSender here
            System.out.println("Sending email to: " + user.getEmail());
            String messageBody = emailTemplateService.buildNotificationTemplate(title, message);
            sendHtmlEmail(
                    user.getEmail(),
                    "App-Notification",
                    messageBody);

        } catch (Exception e) {
            // log error properly
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }

    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        javaMailSender.send(message);
    }

    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true = HTML

            javaMailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send HTML email: " + e.getMessage());
        }
    }
}

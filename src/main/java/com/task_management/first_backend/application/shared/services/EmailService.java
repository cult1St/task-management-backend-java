package com.task_management.first_backend.application.shared.services;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final EmailTemplateService emailTemplateService;

    @Value("${spring.mail.username}")
    private String fromAddress;

    @Async("emailExecutor")
    public void sendVerificationEmail(String toEmail, String code) {
        try {
            System.out.println("Sending verification email to: " + toEmail);
            String messageBody = emailTemplateService.buildVerifyEmailTemplate(code);
            sendHtmlEmail(toEmail, "Verify your TaskFlow email", messageBody);
            System.out.println("Verification code: " + code);
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Async("emailExecutor")
    public void sendWorkspaceInviteEmail(String toEmail, String workspaceName, String inviterName, String role) {
        try {
            System.out.println("Sending workspace invite to: " + toEmail);
            String messageBody = emailTemplateService.buildWorkspaceInviteTemplate(
                    workspaceName, inviterName, role
            );
            sendHtmlEmail(toEmail, "You're invited to join " + workspaceName, messageBody);
        } catch (Exception e) {
            System.err.println("Failed to send workspace invite email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Async("emailExecutor")
    public void sendNotification(String title, String message, String toEmail) {
        try {
            System.out.println("Sending email to: " + toEmail);
            String messageBody = emailTemplateService.buildNotificationTemplate(title, message);
            sendHtmlEmail(toEmail, "TaskFlow notification", messageBody);
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        javaMailSender.send(message);
    }

    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            javaMailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send HTML email: " + e.getMessage());
            e.printStackTrace();
            throw new IllegalStateException("Failed to send email to " + to, e);
        }
    }
}

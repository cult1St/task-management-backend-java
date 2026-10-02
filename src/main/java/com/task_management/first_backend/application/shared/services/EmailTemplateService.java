package com.backend.fintech.application.core.services;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;

@Service
public class EmailTemplateService {

    private String emailHeader(String title) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        line-height: 1.6;
                        color: #333;
                        margin: 0;
                        padding: 0;
                        background: #f4f6f8;
                    }
                    .container {
                        max-width: 600px;
                        margin: 20px auto;
                        background: #ffffff;
                        border-radius: 8px;
                        overflow: hidden;
                        box-shadow: 0 2px 10px rgba(0,0,0,0.05);
                    }
                    .header {
                        background: #1d9e75;
                        color: #ffffff;
                        padding: 25px;
                        text-align: center;
                    }
                    .header h1 {
                        margin: 0;
                        font-size: 22px;
                    }
                    .content {
                        padding: 30px;
                    }
                    .code-box {
                        background: #f9f9f9;
                        border: 2px dashed #1d9e75;
                        padding: 20px;
                        text-align: center;
                        margin: 20px 0;
                        border-radius: 5px;
                    }
                    .code {
                        font-size: 28px;
                        font-weight: bold;
                        letter-spacing: 4px;
                        color: #1d9e75;
                    }
                    .footer {
                        color: #777;
                        font-size: 12px;
                        text-align: center;
                        padding: 20px;
                        border-top: 1px solid #eee;
                        background: #fafafa;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>%s</h1>
                    </div>
                    <div class="content">
        """, title);
    }

    private String emailFooter() {
        int year = Year.now().getValue();

        return String.format("""
                    </div>
                    <div class="footer">
                        <p>&copy; %d PayVault. All rights reserved.</p>
                        <p>This is an automated message. Please do not reply.</p>
                        <p>Need help? <a href="mailto:support@payvault.com">support@payvault.com</a></p>
                    </div>
                </div>
            </body>
            </html>
        """, year);
    }

    // =========================
    // VERIFY EMAIL (CODE ONLY)
    // =========================
    public String buildVerifyEmailTemplate(String code) {
        return String.format("""
            %s
            <p>Welcome to PayVault 👋</p>
            <p>Please verify your email using the code below:</p>

            <div class='code-box'>
                <div class='code'>%s</div>
            </div>

            <p>This code will expire shortly. Do not share it with anyone.</p>
            %s
            """,
                emailHeader("Verify Your Email"),
                code,
                emailFooter()
        );
    }

    // =========================
    // RESET PASSWORD (CODE ONLY)
    // =========================
    public String buildResetPasswordTemplate(String code) {
        return String.format("""
            %s
            <p>We received a request to reset your password.</p>
            <p>Use the code below to proceed:</p>

            <div class='code-box'>
                <div class='code'>%s</div>
            </div>

            <p>If you didn’t request this, please secure your account immediately.</p>
            %s
            """,
                emailHeader("Reset Your Password"),
                code,
                emailFooter()
        );
    }

    // =========================
    // GENERIC NOTIFICATION
    // =========================
    public String buildNotificationTemplate(String title, String message) {
        return String.format("""
            %s
            <p>%s</p>
            %s
            """,
                emailHeader(title),
                message,
                emailFooter()
        );
    }

    // =========================
    // LAST LOGIN ALERT
    // =========================
    public String buildLastLoginTemplate(LocalDateTime loginTime, String ipAddress) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return String.format("""
            %s
            <p>Your account was just accessed.</p>

            <p><strong>Login Time:</strong> %s</p>
            <p><strong>IP Address:</strong> %s</p>

            <p>If this was you, no action is required.</p>
            <p>If you do not recognize this activity, secure your account immediately.</p>
            %s
            """,
                emailHeader("New Login Detected"),
                loginTime.format(formatter),
                ipAddress,
                emailFooter()
        );
    }
}
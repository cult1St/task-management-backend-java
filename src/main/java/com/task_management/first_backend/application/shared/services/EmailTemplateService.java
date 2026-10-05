package com.task_management.first_backend.application.shared.services;

import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Service;

@Service
public class EmailTemplateService {

    private static final String BRAND = "TaskFlow";
    private static final String SUPPORT_EMAIL = "support@taskflow.app";
    private static final String ACCENT = "#2dd4bf";
    private static final String NAVY = "#0c1b32";

    private String emailHeader(String title) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body {
                        font-family: 'DM Sans', Arial, Helvetica, sans-serif;
                        line-height: 1.6;
                        color: #0f2449;
                        margin: 0;
                        padding: 0;
                        background: #f1f5f9;
                    }
                    .container {
                        max-width: 600px;
                        margin: 20px auto;
                        background: #ffffff;
                        border-radius: 12px;
                        overflow: hidden;
                        box-shadow: 0 8px 32px rgba(3, 11, 24, 0.08);
                    }
                    .header {
                        background: %s;
                        color: #ffffff;
                        padding: 28px 25px;
                        text-align: center;
                    }
                    .brand {
                        display: inline-block;
                        margin-bottom: 10px;
                        font-size: 14px;
                        font-weight: 700;
                        letter-spacing: 0.08em;
                        text-transform: uppercase;
                        color: %s;
                    }
                    .header h1 {
                        margin: 0;
                        font-size: 22px;
                        font-weight: 700;
                        color: #ffffff;
                    }
                    .content {
                        padding: 30px;
                    }
                    .code-box {
                        background: #f1f5f9;
                        border: 2px dashed %s;
                        padding: 20px;
                        text-align: center;
                        margin: 20px 0;
                        border-radius: 8px;
                    }
                    .code {
                        font-size: 28px;
                        font-weight: bold;
                        letter-spacing: 4px;
                        color: %s;
                        font-family: 'JetBrains Mono', Consolas, monospace;
                    }
                    a {
                        color: %s;
                    }
                    .footer {
                        color: #64748b;
                        font-size: 12px;
                        text-align: center;
                        padding: 20px;
                        border-top: 1px solid #e2e8f0;
                        background: #f8fafc;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <div class="brand">%s</div>
                        <h1>%s</h1>
                    </div>
                    <div class="content">
        """, NAVY, ACCENT, ACCENT, NAVY, ACCENT, BRAND, title);
    }

    private String emailFooter() {
        int year = Year.now().getValue();

        return String.format("""
                    </div>
                    <div class="footer">
                        <p>&copy; %d %s Inc. All rights reserved.</p>
                        <p>This is an automated message. Please do not reply.</p>
                        <p>Need help? <a href="mailto:%s">%s</a></p>
                    </div>
                </div>
            </body>
            </html>
        """, year, BRAND, SUPPORT_EMAIL, SUPPORT_EMAIL);
    }

    public String buildVerifyEmailTemplate(String code) {
        return String.format("""
            %s
            <p>Welcome to %s 👋</p>
            <p>Please verify your email using the code below:</p>

            <div class='code-box'>
                <div class='code'>%s</div>
            </div>

            <p>This code will expire shortly. Do not share it with anyone.</p>
            %s
            """,
                emailHeader("Verify Your Email"),
                BRAND,
                code,
                emailFooter()
        );
    }

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

    public String buildWorkspaceInviteTemplate(String workspaceName, String inviterName, String role) {
        return String.format("""
            %s
            <p>%s invited you to join <strong>%s</strong> as a <strong>%s</strong> on %s.</p>
            <p>Create an account or sign in to accept the invitation and start collaborating.</p>
            %s
            """,
                emailHeader("Workspace Invitation"),
                inviterName,
                workspaceName,
                role,
                BRAND,
                emailFooter()
        );
    }

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

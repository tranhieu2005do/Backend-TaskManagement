package com.java_spring_boot.first_demo.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
  private final JavaMailSender mailSender;

  public void sendOtp(String toEmail, String otp) throws MessagingException {
    log.info("Sending OTP to email {}", toEmail);
    String content = """
        <!DOCTYPE html>
        <html>
        <head>
          <meta charset="UTF-8">
        </head>
        <body style="margin:0; padding:0; font-family:Arial, sans-serif; background-color:#f4f6f8;">
          <div style="max-width:600px; margin:40px auto; background:#ffffff; border-radius:12px; overflow:hidden; box-shadow:0 4px 12px rgba(0,0,0,0.1);">

            <!-- Header -->
            <div style="background:#4f46e5; padding:20px; text-align:center; color:white;">
              <h2 style="margin:0;">🔐 Password Reset</h2>
            </div>

            <!-- Body -->
            <div style="padding:30px; text-align:center;">
              <p style="font-size:16px; color:#333;">
                You requested to reset your password.
              </p>

              <p style="font-size:16px; color:#333;">
                Use the OTP below:
              </p>

              <!-- OTP Box -->
              <div style="margin:20px 0;">
                <span style="
                  display:inline-block;
                  padding:15px 30px;
                  font-size:28px;
                  font-weight:bold;
                  letter-spacing:4px;
                  background:#f1f5f9;
                  border-radius:8px;
                  color:#111827;
                ">
                  %s
                </span>
              </div>

              <p style="font-size:14px; color:#6b7280;">
                This code will expire in 5 minutes.
              </p>
            </div>

            <!-- Footer -->
            <div style="background:#f9fafb; padding:15px; text-align:center; font-size:12px; color:#9ca3af;">
              If you didn't request this, please ignore this email.
            </div>

          </div>
        </body>
        </html>
        """
        .formatted(otp);
    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true);

    helper.setTo(toEmail);
    helper.setSubject("Reset Password OTP");

    helper.setText(content, true);
    helper.setFrom("dotranhieu925@gmail.com");

    mailSender.send(message);
  }

  public void sendActiveAccountMail(String toEmail, String token) {

    String link = "http://localhost:8080/api/v1/auth/verify?token=" + token;

    String subject = "Verify your account";
    String htmlBody = """
        <!DOCTYPE html>
        <html>
        <head>
          <meta charset="UTF-8">
        </head>
        <body style="margin:0; padding:0; font-family:Arial, sans-serif; background-color:#f4f6f8;">
          <div style="max-width:600px; margin:40px auto; background:#ffffff; border-radius:12px; overflow:hidden; box-shadow:0 4px 12px rgba(0,0,0,0.1);">
            <div style="background:#4f46e5; padding:20px; text-align:center; color:white;">
              <h2 style="margin:0;">🚀 Khởi tạo tài khoản thành công</h2>
            </div>
            <div style="padding:30px; text-align:center;">
              <p style="font-size:16px; color:#333;">
                Chào mừng bạn! Vui lòng nhấn vào nút bên dưới để kích hoạt tài khoản của bạn.
              </p>
              <div style="margin:30px 0;">
                <a href="%s" style="
                  display:inline-block;
                  padding:14px 28px;
                  font-size:16px;
                  font-weight:bold;
                  color:#ffffff;
                  background-color:#4f46e5;
                  border-radius:8px;
                  text-decoration:none;
                ">
                  Kích hoạt tài khoản
                </a>
              </div>
              <p style="font-size:14px; color:#6b7280;">
                Liên kết này sẽ sớm hết hạn.
              </p>
            </div>
            <div style="background:#f9fafb; padding:15px; text-align:center; font-size:12px; color:#9ca3af;">
              Nếu bạn không yêu cầu tạo tài khoản, xin vui lòng bỏ qua email này.
            </div>
          </div>
        </body>
        </html>
        """.formatted(link);

    sendHtmlEmail(toEmail, subject, htmlBody);
  }

  private void sendHtmlEmail(String to, String subject, String htmlBody) {
    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
      
      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(htmlBody, true);
      helper.setFrom("dotranhieu925@gmail.com");

      mailSender.send(message);

    } catch (Exception e) {
      log.error("Email sending failed: {}", e.getMessage());
      throw new RuntimeException("Cannot send email", e);
    }
  }

  private void sendEmail(String to, String subject, String body) {
    try {
      SimpleMailMessage message = new SimpleMailMessage();

      message.setTo(to);
      message.setSubject(subject);
      message.setText(body);

      mailSender.send(message);

    } catch (Exception e) {
      log.error("Email sending failed: {}", e.getMessage());
      throw new RuntimeException("Cannot send email");
    }
  }

  private String buildEmailBody(String link) {
    return """
        Welcome!

        Please verify your account by clicking the link below:

        %s

        This link will expire soon.

        If you did not request this, ignore this email.
        """.formatted(link);
  }
}

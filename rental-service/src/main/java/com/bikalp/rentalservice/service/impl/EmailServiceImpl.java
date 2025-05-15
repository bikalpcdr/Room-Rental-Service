package com.bikalp.rentalservice.service.impl;

import com.bikalp.rentalservice.exception.EmailException;
import com.bikalp.rentalservice.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Override
    public void sendPasswordResetEmail(String toEmail, String resetUrl) throws EmailException {
        try {
            validateEmailConfiguration();
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Password Reset Request");

            String htmlContent = String.format(
                    "<html><body>" +
                            "<h2>Password Reset Request</h2>" +
                            "<p>You have requested to reset your password. Click the link below to proceed:</p>" +
                            "<p><a href='%s'>Reset Password</a></p>" +
                            "<p>If you did not request this, please ignore this email.</p>" +
                            "</body></html>",
                    resetUrl
            );

            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("Password reset email sent to: {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send password reset email to: {}", toEmail, e);
            throw new EmailException("Failed to send password reset email", e);
        }
    }

    @Override
    public void sendVerificationEmail(String toEmail, String verificationUrl) throws EmailException {
        try {
            validateEmailConfiguration();
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Email Verification");

            String htmlContent = String.format(
                    "<html><body>" +
                            "<h2>Email Verification</h2>" +
                            "<p>Please click the link below to verify your email address:</p>" +
                            "<p><a href='%s'>Verify Email</a></p>" +
                            "<p>If you did not create an account, please ignore this email.</p>" +
                            "</body></html>",
                    verificationUrl
            );

            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("Verification email sent to: {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send verification email to: {}", toEmail, e);
            throw new EmailException("Failed to send verification email", e);
        }
    }

    @Override
    public void sendWelcomeEmail(String toEmail, String subject, String htmlContent) throws EmailException {
        try {
            validateEmailConfiguration();
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Welcome email sent to: {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send welcome email to: {}", toEmail, e);
            throw new EmailException("Failed to send welcome email", e);
        }
    }

    @Override
    public void sendBookingConfirmationEmail(String toEmail, String subject, String htmlContent) throws EmailException {
        try {
            validateEmailConfiguration();
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Booking confirmation email sent to: {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send booking confirmation email to: {}", toEmail, e);
            throw new EmailException("Failed to send booking confirmation email", e);
        }
    }

    @Override
    public void sendBookingCancellationEmail(String toEmail, String subject, String htmlContent) throws EmailException {
        try {
            validateEmailConfiguration();
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Booking cancellation email sent to: {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send booking cancellation email to: {}", toEmail, e);
            throw new EmailException("Failed to send booking cancellation email", e);
        }
    }

    private void validateEmailConfiguration() {
        if (!StringUtils.hasText(fromEmail) || fromEmail.equals("bikalpcdr42@gmail.com")) {
            throw new EmailException("Email configuration is not properly set up. Please check application.properties");
        }
    }
} 
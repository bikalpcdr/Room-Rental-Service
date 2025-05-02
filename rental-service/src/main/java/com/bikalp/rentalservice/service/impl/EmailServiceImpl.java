package com.bikalp.rentalservice.service.impl;

import com.bikalp.rentalservice.service.EmailService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final Configuration freemarkerConfig;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Override
    public void sendPasswordResetEmail(String toEmail, String resetUrl) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Password Reset Request");

            Map<String, Object> model = new HashMap<>();
            model.put("resetUrl", resetUrl);
            model.put("frontendUrl", frontendUrl);

            Template template = freemarkerConfig.getTemplate("password-reset-email.ftl");
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);

            helper.setText(html, true);
            mailSender.send(message);
        } catch (MessagingException | IOException | TemplateException e) {
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    @Override
    public void sendVerificationEmail(String toEmail, String verificationUrl) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Email Verification");

            Map<String, Object> model = new HashMap<>();
            model.put("verificationUrl", verificationUrl);
            model.put("frontendUrl", frontendUrl);

            Template template = freemarkerConfig.getTemplate("verification-email.ftl");
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);

            helper.setText(html, true);
            mailSender.send(message);
        } catch (MessagingException | IOException | TemplateException e) {
            throw new RuntimeException("Failed to send verification email", e);
        }
    }

    @Override
    public void sendWelcomeEmail(String email, String name) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            Map<String, Object> model = new HashMap<>();
            model.put("name", name);
            model.put("baseUrl", frontendUrl);
            
            Template template = freemarkerConfig.getTemplate("email/welcome.html");
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
            
            helper.setFrom(fromEmail);
            helper.setTo(email);
            helper.setSubject("Welcome to Rental Service");
            helper.setText(html, true);
            
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send welcome email", e);
        }
    }

    @Override
    public void sendBookingConfirmationEmail(String email, String bookingDetails) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            Map<String, Object> model = new HashMap<>();
            model.put("bookingDetails", bookingDetails);
            model.put("baseUrl", frontendUrl);
            
            Template template = freemarkerConfig.getTemplate("email/booking-confirmation.html");
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
            
            helper.setFrom(fromEmail);
            helper.setTo(email);
            helper.setSubject("Booking Confirmation");
            helper.setText(html, true);
            
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send booking confirmation email", e);
        }
    }

    @Override
    public void sendBookingCancellationEmail(String email, String bookingDetails) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            Map<String, Object> model = new HashMap<>();
            model.put("bookingDetails", bookingDetails);
            model.put("baseUrl", frontendUrl);
            
            Template template = freemarkerConfig.getTemplate("email/booking-cancellation.html");
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
            
            helper.setFrom(fromEmail);
            helper.setTo(email);
            helper.setSubject("Booking Cancellation");
            helper.setText(html, true);
            
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send booking cancellation email", e);
        }
    }
} 
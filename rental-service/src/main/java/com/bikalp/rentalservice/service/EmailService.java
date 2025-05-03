package com.bikalp.rentalservice.service;

import com.bikalp.rentalservice.exception.EmailException;

public interface EmailService {
    /**
     * Sends a password reset email to the user
     * @param toEmail The recipient's email address
     * @param resetUrl The password reset URL
     * @throws EmailException If email sending fails
     */
    void sendPasswordResetEmail(String toEmail, String resetUrl) throws EmailException;

    /**
     * Sends an email verification link to the user
     * @param toEmail The recipient's email address
     * @param verificationUrl The email verification URL
     * @throws EmailException If email sending fails
     */
    void sendVerificationEmail(String toEmail, String verificationUrl) throws EmailException;

    /**
     * Sends a welcome email to newly registered users
     * @param toEmail The recipient's email address
     * @param subject The email subject
     * @param htmlContent The HTML content of the email
     * @throws EmailException If email sending fails
     */
    void sendWelcomeEmail(String toEmail, String subject, String htmlContent) throws EmailException;

    /**
     * Sends a booking confirmation email
     * @param toEmail The recipient's email address
     * @param subject The email subject
     * @param htmlContent The HTML content of the email
     * @throws EmailException If email sending fails
     */
    void sendBookingConfirmationEmail(String toEmail, String subject, String htmlContent) throws EmailException;

    /**
     * Sends a booking cancellation email
     * @param toEmail The recipient's email address
     * @param subject The email subject
     * @param htmlContent The HTML content of the email
     * @throws EmailException If email sending fails
     */
    void sendBookingCancellationEmail(String toEmail, String subject, String htmlContent) throws EmailException;
} 
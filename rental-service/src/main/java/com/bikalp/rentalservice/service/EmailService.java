package com.bikalp.rentalservice.service;

public interface EmailService {
    /**
     * Sends a password reset email to the user
     * @param toEmail The recipient's email address
     * @param resetUrl The password reset URL
     */
    void sendPasswordResetEmail(String toEmail, String resetUrl);

    /**
     * Sends an email verification link to the user
     * @param toEmail The recipient's email address
     * @param verificationUrl The email verification URL
     */
    void sendVerificationEmail(String toEmail, String verificationUrl);

    /**
     * Sends a welcome email to newly registered users
     * @param email The recipient's email address
     * @param name The user's name
     */
    void sendWelcomeEmail(String email, String name);

    /**
     * Sends a booking confirmation email
     * @param email The recipient's email address
     * @param bookingDetails The booking details
     */
    void sendBookingConfirmationEmail(String email, String bookingDetails);

    /**
     * Sends a booking cancellation email
     * @param email The recipient's email address
     * @param bookingDetails The booking details
     */
    void sendBookingCancellationEmail(String email, String bookingDetails);
} 
package com.bikalp.rentalservice.service;

import com.bikalp.rentalservice.dto.LoginRequest;
import com.bikalp.rentalservice.dto.RegisterRequest;
import com.bikalp.rentalservice.dto.ResetPasswordRequest;
import com.bikalp.rentalservice.entity.User;
import com.bikalp.rentalservice.exception.AuthenticationException;
import com.bikalp.rentalservice.exception.UserAlreadyExistsException;

public interface AuthService {
    /**
     * Authenticates a user with the provided credentials
     *
     * @param loginRequest The login request containing email and password
     * @throws AuthenticationException If authentication fails
     */
    void login(LoginRequest loginRequest) throws AuthenticationException;

    /**
     * Registers a new user
     *
     * @param registerRequest The registration request containing user details
     * @return The created user
     * @throws UserAlreadyExistsException If a user with the same email already exists
     */
    User register(RegisterRequest registerRequest) throws UserAlreadyExistsException;

    /**
     * Sends a password reset email to the user
     *
     * @param email The user's email address
     * @throws AuthenticationException If the email is not found
     */
    void sendPasswordResetEmail(String email) throws AuthenticationException;

    /**
     * Resets the user's password using the provided token
     *
     * @param token                The password reset token
     * @param resetPasswordRequest The new password details
     * @throws AuthenticationException If the token is invalid or expired
     */
    void resetPassword(String token, ResetPasswordRequest resetPasswordRequest) throws AuthenticationException;

    /**
     * Logs out the current user
     */
    void logout();

    /**
     * Gets the currently authenticated user
     *
     * @return The current user or null if not authenticated
     */
    User getCurrentUser();

    /**
     * Checks if the current user is authenticated
     *
     * @return true if authenticated, false otherwise
     */
    boolean isAuthenticated();
} 
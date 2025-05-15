package com.bikalp.rentalservice.service.impl;

import com.bikalp.rentalservice.dto.LoginRequest;
import com.bikalp.rentalservice.dto.RegisterRequest;
import com.bikalp.rentalservice.dto.ResetPasswordRequest;
import com.bikalp.rentalservice.entity.PasswordResetToken;
import com.bikalp.rentalservice.entity.User;
import com.bikalp.rentalservice.enums.UserRole;
import com.bikalp.rentalservice.exception.AuthenticationException;
import com.bikalp.rentalservice.exception.InvalidTokenException;
import com.bikalp.rentalservice.exception.TokenExpiredException;
import com.bikalp.rentalservice.exception.UserAlreadyExistsException;
import com.bikalp.rentalservice.repository.PasswordResetTokenRepository;
import com.bikalp.rentalservice.repository.UserRepo;
import com.bikalp.rentalservice.service.AuthService;
import com.bikalp.rentalservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepo userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;

    @Override
    public void login(LoginRequest loginRequest) throws AuthenticationException {
        try {
            log.info("Starting authentication process for: {}", loginRequest.getEmailOrUsername());

            // First try to find user by email
            User user = userRepository.findByEmail(loginRequest.getEmailOrUsername())
                    .orElseGet(() -> {
                        log.info("User not found by email, trying username");
                        return userRepository.findByUsername(loginRequest.getEmailOrUsername())
                                .orElseThrow(() -> {
                                    log.warn("User not found with email/username: {}", loginRequest.getEmailOrUsername());
                                    return new AuthenticationException("User not found");
                                });
                    });

            log.info("User found: {} with role: {}", user.getUsername(), user.getRole());

            if (!user.isEnabled()) {
                log.warn("User account is disabled: {}", user.getUsername());
                throw new AuthenticationException("Account is disabled");
            }

            // Verify password
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                log.warn("Invalid password for user: {}", user.getUsername());
                throw new AuthenticationException("Invalid password");
            }

            // Create authentication token
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    user.getUsername(), // Use username for authentication
                    loginRequest.getPassword(),
                    user.getAuthorities()
            );

            // Authenticate
            Authentication authentication = authenticationManager.authenticate(authToken);
            log.info("Authentication successful for user: {}", authentication.getName());

            // Set authentication in security context
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (AuthenticationException e) {
            log.error("Authentication failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during authentication: {}", e.getMessage(), e);
            throw new AuthenticationException("Authentication failed");
        }
    }

    @Override
    @Transactional
    public User register(RegisterRequest registerRequest) throws UserAlreadyExistsException {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new AuthenticationException("Passwords do not match");
        }

        User user = new User();
        user.setUsername(registerRequest.getEmail());
        user.setFullName(registerRequest.getName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setPhoneNumber(registerRequest.getPhoneNumber());
        user.setRole(UserRole.CUSTOMER);
        user.setEnabled(true);

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void sendPasswordResetEmail(String email) throws AuthenticationException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException("User not found"));

        // Delete any existing tokens for this user
        passwordResetTokenRepository.deleteByUser(user);

        // Create new token
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(24));
        passwordResetTokenRepository.save(resetToken);

        // Send email
        String resetUrl = "http://localhost:8080/auth/reset-password?token=" + token;
        emailService.sendPasswordResetEmail(user.getEmail(), resetUrl);
    }

    @Override
    @Transactional
    public void resetPassword(String token, ResetPasswordRequest resetPasswordRequest) throws AuthenticationException {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid or expired token"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            passwordResetTokenRepository.delete(resetToken);
            throw new TokenExpiredException("Token has expired");
        }

        if (!resetPasswordRequest.getPassword().equals(resetPasswordRequest.getConfirmPassword())) {
            throw new AuthenticationException("Passwords do not match");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(resetPasswordRequest.getPassword()));
        userRepository.save(user);

        // Delete the used token
        passwordResetTokenRepository.delete(resetToken);
    }

    @Override
    public void logout() {
        SecurityContextHolder.clearContext();
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            return userRepository.findByEmail(email).orElse(null);
        }
        return null;
    }

    @Override
    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }
} 
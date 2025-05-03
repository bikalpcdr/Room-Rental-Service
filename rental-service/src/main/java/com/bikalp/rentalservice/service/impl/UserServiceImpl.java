package com.bikalp.rentalservice.service.impl;

import com.bikalp.rentalservice.entity.User;
import com.bikalp.rentalservice.enums.UserRole;
import com.bikalp.rentalservice.exception.EmailException;
import com.bikalp.rentalservice.exception.ResourceNotFoundException;
import com.bikalp.rentalservice.exception.UserAlreadyExistsException;
import com.bikalp.rentalservice.repository.UserRepo;
import com.bikalp.rentalservice.service.EmailService;
import com.bikalp.rentalservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final TemplateEngine templateEngine;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Value("${app.base-url}")
    private String baseUrl;

    @Override
    public void saveUser(User user) {
        logger.info("Saving user: {}", user.getUsername());
        validateUserUniqueness(user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepo.save(user);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        logger.info("Fetching user by ID: {}", id);
        return userRepo.findById(id);
    }

    @Override
    public List<User> getAllUsers() {
        logger.info("Fetching all users");
        return userRepo.findAll();
    }

    @Override
    public void updateUser(Long id, User user) {
        logger.info("Updating user with ID: {}", id);
        User existingUser = userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        // Check if username or email is being changed to an existing one
        if (!existingUser.getUsername().equals(user.getUsername()) && existsByUsername(user.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists");
        }
        if (!existingUser.getEmail().equals(user.getEmail()) && existsByEmail(user.getEmail())) {
            throw new EmailException("Email already exists");
        }
        
        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        existingUser.setFullName(user.getFullName());
        existingUser.setPhoneNumber(user.getPhoneNumber());
        existingUser.setProfilePicture(user.getProfilePicture());
        existingUser.setRole(user.getRole());
        
        // Only update password if a new one is provided
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        userRepo.save(existingUser);
    }

    @Override
    public void deleteUser(Long id) {
        logger.info("Deleting user with ID: {}", id);
        if (!userRepo.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepo.deleteById(id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        logger.debug("Finding user by username: {}", username);
        return userRepo.findByUsername(username);
    }

    @Override
    public List<User> getUsersByRole(UserRole role) {
        logger.info("Fetching users by role: {}", role);
        return userRepo.findByRole(role);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepo.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepo.existsByEmail(email);
    }

    @Override
    public long getTotalUsers() {
        logger.info("Fetching total user count");
        return userRepo.count();
    }

    @Override
    public List<User> getRecentUsers(int limit) {
        logger.info("Fetching {} most recent users", limit);
        return userRepo.findAll(
            PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "id"))
        ).getContent();
    }

    @Override
    public Optional<User> findById(Long id) {
        logger.info("Finding user by ID: {}", id);
        Optional<User> user = userRepo.findById(id);
        if (user.isPresent()) {
            logger.info("Found user: {}", user.get().getUsername());
        } else {
            logger.warn("No user found with ID: {}", id);
        }
        return user;
    }

    @Override
    public User save(User user) {
        return userRepo.save(user);
    }

    @Override
    public void delete(Long id) {
        userRepo.deleteById(id);
    }

    @Override
    public Page<User> findAllUsers(Pageable pageable, String search) {
        logger.info("Searching users with criteria: {}", search);
        if (search != null && !search.isEmpty()) {
            return userRepo.findByUsernameContainingOrEmailContainingOrFullNameContaining(
                search, search, search, pageable
            );
        }
        return userRepo.findAll(pageable);
    }

    @Override
    public void createUser(User user) {
        logger.info("Creating new user: {}", user.getUsername());
        validateUserUniqueness(user);
        
        String plainPassword = user.getPassword();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(true);
        User savedUser = save(user);

        sendWelcomeEmail(savedUser, plainPassword);
    }

    @Override
    public void toggleUserStatus(Long id) {
        logger.info("Toggling user status for ID: {}", id);
        User user = findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setEnabled(!user.isEnabled());
        save(user);
    }

    @Override
    public void changeUserRoleToLandlord(Long id) {
        logger.info("Changing user role to landlord for ID: {}", id);
        User user = findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setRole(UserRole.LANDLORD);
        save(user);
    }

    private void validateUserUniqueness(User user) {
        if (existsByUsername(user.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists");
        }
        if (existsByEmail(user.getEmail())) {
            throw new EmailException("Email already exists");
        }
    }

    private void sendWelcomeEmail(User user, String plainPassword) {
        try {
            Context context = new Context();
            context.setVariable("name", user.getFullName());
            context.setVariable("username", user.getUsername());
            context.setVariable("password", plainPassword);
            context.setVariable("baseUrl", baseUrl);

            String htmlContent = templateEngine.process("email/welcome", context);
            emailService.sendWelcomeEmail(user.getEmail(), "Welcome to Rental Service", htmlContent);
            logger.info("Welcome email sent successfully to user: {}", user.getEmail());
        } catch (Exception e) {
            logger.error("Failed to send welcome email to user: {}", user.getEmail(), e);
        }
    }
} 
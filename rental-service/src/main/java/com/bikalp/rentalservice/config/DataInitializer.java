package com.bikalp.rentalservice.config;

import com.bikalp.rentalservice.entity.User;
import com.bikalp.rentalservice.enums.UserRole;
import com.bikalp.rentalservice.repository.RoomRepo;
import com.bikalp.rentalservice.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepo userRepository;
    private final RoomRepo roomRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Create superadmin user if not exists
        if (!userRepository.existsByEmail("superadmin@rentalservice.com")) {
            log.info("Creating superadmin user...");
            User superadmin = new User();
            superadmin.setUsername("superadmin");
            superadmin.setFullName("Super Admin");
            superadmin.setEmail("superadmin@rentalservice.com");
            superadmin.setPassword(passwordEncoder.encode("SuperAdmin@123"));
            superadmin.setPhoneNumber("+1234567890");
            superadmin.setRole(UserRole.SUPER_ADMIN);
            superadmin.setEnabled(true);
            userRepository.save(superadmin);
            log.info("Superadmin user created successfully!");
        } else {
            log.info("Superadmin user already exists");
        }
    }
} 
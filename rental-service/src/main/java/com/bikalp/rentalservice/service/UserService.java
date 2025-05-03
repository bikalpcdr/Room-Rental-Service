package com.bikalp.rentalservice.service;

import com.bikalp.rentalservice.entity.User;
import com.bikalp.rentalservice.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User saveUser(User user);
    Optional<User> getUserById(Long id);
    List<User> getAllUsers();
    User updateUser(Long id, User user);
    void deleteUser(Long id);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> getUsersByRole(UserRole role);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    long getTotalUsers();
    List<User> getRecentUsers(int limit);
    Optional<User> findById(Long id);
    User save(User user);
    void delete(Long id);
    Page<User> findAllUsers(Pageable pageable, String search);
    void createUser(User user);
    void toggleUserStatus(Long id);
} 
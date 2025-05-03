package com.bikalp.rentalservice.service;

import com.bikalp.rentalservice.entity.User;
import com.bikalp.rentalservice.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {
    void saveUser(User user);

    Optional<User> getUserById(Long id);

    List<User> getAllUsers();

    void updateUser(Long id, User user);

    void deleteUser(Long id);

    Optional<User> findByUsername(String username);

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

    void changeUserRoleToLandlord(Long id);
} 
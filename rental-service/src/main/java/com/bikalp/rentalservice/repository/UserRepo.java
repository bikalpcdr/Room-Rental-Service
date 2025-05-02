package com.bikalp.rentalservice.repository;

import com.bikalp.rentalservice.entity.User;
import com.bikalp.rentalservice.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for User entity.
 * Provides data access operations for users.
 */
@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    List<User> findByRole(UserRole role);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query(value = "SELECT * FROM users u WHERE u.enabled = true AND u.username = :username",
            nativeQuery = true)
    Optional<User> findEnabledByUsername(@Param("username") String username);

    @Query(value = "SELECT * FROM users u WHERE u.enabled = true AND u.email = :email",
            nativeQuery = true)
    Optional<User> findEnabledByEmail(@Param("email") String email);

    @Query(value = "SELECT * FROM users u WHERE u.enabled = true AND u.role = :role",
            nativeQuery = true)
    List<User> findEnabledByRole(@Param("role") String role);

    @Query(value = "SELECT * FROM users u WHERE u.enabled = true",
            nativeQuery = true)
    List<User> findAllEnabledUsers();
}

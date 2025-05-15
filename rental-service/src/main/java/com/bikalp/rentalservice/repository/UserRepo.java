package com.bikalp.rentalservice.repository;

import com.bikalp.rentalservice.entity.User;
import com.bikalp.rentalservice.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    @Query(value = "select * from users where username = :username",nativeQuery = true)
    Optional<User> findByUsername(String username);

    @Query(value = "select * from users where email = :email",nativeQuery = true)
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

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
    long countByRole(@Param("role") UserRole role);

    @Query("SELECT COUNT(u) FROM User u WHERE u.enabled = true")
    long countActiveUsers();

    @Query("SELECT u FROM User u ORDER BY u.id DESC")
    List<User> findRecentUsers(Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.username LIKE %:keyword% OR u.email LIKE %:keyword% OR u.fullName LIKE %:keyword%")
    List<User> searchUsers(@Param("keyword") String keyword);

    @Query("SELECT u FROM User u WHERE u.role = :role AND u.enabled = true")
    List<User> findActiveUsersByRole(@Param("role") UserRole role);

    Page<User> findByUsernameContainingOrEmailContainingOrFullNameContaining(
        String username, String email, String fullName, Pageable pageable
    );
}

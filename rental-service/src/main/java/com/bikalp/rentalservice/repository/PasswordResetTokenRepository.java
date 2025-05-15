package com.bikalp.rentalservice.repository;

import com.bikalp.rentalservice.entity.PasswordResetToken;
import com.bikalp.rentalservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);

    void deleteByUser(User user);
} 
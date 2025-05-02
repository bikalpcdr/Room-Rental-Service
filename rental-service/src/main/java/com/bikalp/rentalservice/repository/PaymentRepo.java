package com.bikalp.rentalservice.repository;

import com.bikalp.rentalservice.entity.Payment;
import com.bikalp.rentalservice.enums.PaymentMethod;
import com.bikalp.rentalservice.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for Payment entity.
 * Provides data access operations for payments.
 */
@Repository
public interface PaymentRepo extends JpaRepository<Payment, Long> {
    List<Payment> findByStatus(PaymentStatus status);

    List<Payment> findByBookingId(Long bookingId);

    @Query(value = "SELECT p.* FROM payments p " +
            "JOIN bookings b ON p.booking_id = b.id " +
            "WHERE b.customer_id = :customerId", 
            nativeQuery = true)
    List<Payment> findByBookingCustomerId(@Param("customerId") Long customerId);

    @Query(value = "SELECT p.* FROM payments p " +
            "WHERE p.booking_id = :bookingId AND p.status = :status", 
            nativeQuery = true)
    List<Payment> findByBookingIdAndStatus(@Param("bookingId") Long bookingId, 
                                         @Param("status") String status);

    @Query(value = "SELECT p.* FROM payments p " +
            "JOIN bookings b ON p.booking_id = b.id " +
            "JOIN rooms r ON b.room_id = r.id " +
            "WHERE r.landlord_id = :landlordId", 
            nativeQuery = true)
    List<Payment> findByLandlordId(@Param("landlordId") Long landlordId);

    List<Payment> findByPaymentMethod(PaymentMethod method);
    
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = 'COMPLETED'")
    BigDecimal getTotalCompletedPayments();
    
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = 'COMPLETED' AND p.paymentDate BETWEEN :startDate AND :endDate")
    BigDecimal getRevenueBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT p FROM Payment p ORDER BY p.paymentDate DESC")
    List<Payment> findRecentPayments(Pageable pageable);
    
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = :status")
    long countByStatus(@Param("status") PaymentStatus status);
    
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.paymentMethod = :method")
    long countByPaymentMethod(@Param("method") PaymentMethod method);
    
    @Query("SELECT p FROM Payment p WHERE p.paymentDate BETWEEN :startDate AND :endDate")
    List<Payment> findPaymentsBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}

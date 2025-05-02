package com.bikalp.rentalservice.repository;

import com.bikalp.rentalservice.entity.Payment;
import com.bikalp.rentalservice.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
            "WHERE b.customer_id = :customerId AND p.status = :status", 
            nativeQuery = true)
    List<Payment> findByCustomerIdAndStatus(@Param("customerId") Long customerId, 
                                          @Param("status") String status);

    @Query(value = "SELECT p.* FROM payments p " +
            "JOIN bookings b ON p.booking_id = b.id " +
            "JOIN rooms r ON b.room_id = r.id " +
            "WHERE r.landlord_id = :landlordId", 
            nativeQuery = true)
    List<Payment> findByLandlordId(@Param("landlordId") Long landlordId);
}

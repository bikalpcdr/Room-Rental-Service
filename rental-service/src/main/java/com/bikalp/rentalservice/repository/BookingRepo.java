package com.bikalp.rentalservice.repository;

import com.bikalp.rentalservice.entity.Booking;
import com.bikalp.rentalservice.enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repository for Booking entity.
 * Provides data access operations for bookings.
 */
@Repository
public interface BookingRepo extends JpaRepository<Booking, Long> {
    List<Booking> findByCustomerId(Long customerId);

    List<Booking> findByRoomId(Long roomId);

    List<Booking> findByStatus(BookingStatus status);

    @Query(value = "SELECT b.* FROM bookings b " +
            "JOIN rooms r ON b.room_id = r.id " +
            "WHERE r.landlord_id = :landlordId", 
            nativeQuery = true)
    List<Booking> findByRoomLandlordId(@Param("landlordId") Long landlordId);

    @Query(value = "SELECT b.* FROM bookings b " +
            "JOIN rooms r ON b.room_id = r.id " +
            "WHERE b.customer_id = :customerId AND b.status = :status", 
            nativeQuery = true)
    List<Booking> findByCustomerIdAndStatus(@Param("customerId") Long customerId, 
                                          @Param("status") String status);

    @Query(value = "SELECT b.* FROM bookings b " +
            "JOIN rooms r ON b.room_id = r.id " +
            "WHERE r.landlord_id = :landlordId AND b.status = :status", 
            nativeQuery = true)
    List<Booking> findByLandlordIdAndStatus(@Param("landlordId") Long landlordId, 
                                          @Param("status") String status);

    @Query(value = "SELECT b.* FROM bookings b " +
            "WHERE b.room_id = :roomId AND b.status = :status", 
            nativeQuery = true)
    List<Booking> findByRoomIdAndStatus(@Param("roomId") Long roomId, 
                                      @Param("status") String status);

    @Query("SELECT COALESCE(SUM(b.totalPrice), 0) FROM Booking b WHERE b.status = 'COMPLETED'")
    BigDecimal getTotalRevenue();
    
    @Query("SELECT b FROM Booking b WHERE b.status = :status ORDER BY b.bookingDate DESC")
    List<Booking> findRecentBookingsByStatus(@Param("status") BookingStatus status, Pageable pageable);
}

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

    /**
     * Count bookings by status
     * @param status booking status
     * @return count of bookings
     */
    long countByStatus(BookingStatus status);

    /**
     * Sum total amount of bookings by status
     * @param status booking status
     * @return sum of total amounts
     */
    @Query("SELECT COALESCE(SUM(b.totalPrice), 0) FROM Booking b WHERE b.status = :status")
    BigDecimal sumTotalAmountByStatus(@Param("status") BookingStatus status);

    /**
     * Count bookings by user ID and status
     * @param userId the user ID
     * @param status the booking status
     * @return count of bookings
     */
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.customer.id = :userId AND b.status = :status")
    long countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") BookingStatus status);

    /**
     * Count all bookings by user ID
     * @param userId the user ID
     * @return count of bookings
     */
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.customer.id = :userId")
    long countByUserId(@Param("userId") Long userId);

    /**
     * Sum total amount of bookings by user ID
     * @param userId the user ID
     * @return sum of total amounts
     */
    @Query("SELECT COALESCE(SUM(b.totalPrice), 0) FROM Booking b WHERE b.customer.id = :userId")
    BigDecimal sumTotalAmountByUserId(@Param("userId") Long userId);

    /**
     * Find bookings by user ID ordered by creation date
     * @param userId the user ID
     * @param pageable pagination information
     * @return page of bookings
     */
    @Query("SELECT b FROM Booking b WHERE b.customer.id = :userId ORDER BY b.bookingDate DESC")
    Page<Booking> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId, Pageable pageable);

    /**
     * Get recent bookings
     * @param limit number of bookings to return
     * @return list of recent bookings
     */
    @Query("SELECT b FROM Booking b ORDER BY b.bookingDate DESC")
    List<Booking> findRecentBookings(Pageable pageable);

    /**
     * Count bookings by landlord ID and status
     * @param landlordId the landlord ID
     * @param status the booking status
     * @return count of bookings
     */
    @Query("SELECT COUNT(b) FROM Booking b JOIN b.room r WHERE r.landlord.id = :landlordId AND b.status = :status")
    long countByLandlordIdAndStatus(@Param("landlordId") Long landlordId, @Param("status") BookingStatus status);

    /**
     * Sum total amount of bookings by landlord ID
     * @param landlordId the landlord ID
     * @return sum of total amounts
     */
    @Query("SELECT COALESCE(SUM(b.totalPrice), 0) FROM Booking b JOIN b.room r WHERE r.landlord.id = :landlordId")
    BigDecimal sumTotalAmountByLandlordId(@Param("landlordId") Long landlordId);

    /**
     * Find bookings by landlord ID ordered by creation date
     * @param landlordId the landlord ID
     * @param pageable pagination information
     * @return page of bookings
     */
    @Query("SELECT b FROM Booking b JOIN b.room r WHERE r.landlord.id = :landlordId ORDER BY b.bookingDate DESC")
    Page<Booking> findByLandlordIdOrderByCreatedAtDesc(@Param("landlordId") Long landlordId, Pageable pageable);

    /**
     * Find all bookings by landlord ID
     * @param landlordId the landlord ID
     * @return list of bookings
     */
    @Query("SELECT b FROM Booking b JOIN b.room r WHERE r.landlord.id = :landlordId")
    List<Booking> findByLandlordId(@Param("landlordId") Long landlordId);
}

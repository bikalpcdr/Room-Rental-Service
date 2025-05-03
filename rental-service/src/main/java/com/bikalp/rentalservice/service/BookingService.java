package com.bikalp.rentalservice.service;

import com.bikalp.rentalservice.entity.Booking;

import java.math.BigDecimal;
import java.util.List;

public interface BookingService {
    long getTotalBookings();

    Booking save(Booking booking);

    void delete(Long id);

    /**
     * Get the count of active bookings
     *
     * @return number of active bookings
     */
    long getActiveBookingsCount();

    BigDecimal getTotalRevenue();

    /**
     * Get recent bookings
     *
     * @param limit number of bookings to return
     * @return list of recent bookings
     */
    List<Booking> getRecentBookings(int limit);

    /**
     * Get the count of active bookings for a specific user
     *
     * @param userId the user ID
     * @return number of active bookings
     */
    long getActiveBookingsCountByUser(Long userId);

    /**
     * Get the total number of bookings for a specific user
     *
     * @param userId the user ID
     * @return total number of bookings
     */
    long getTotalBookingsByUser(Long userId);

    /**
     * Get the total amount spent by a specific user
     *
     * @param userId the user ID
     * @return total amount spent
     */
    double getTotalSpentByUser(Long userId);

    /**
     * Get recent bookings for a specific user
     *
     * @param userId the user ID
     * @param limit  number of bookings to return
     * @return list of recent bookings
     */
    List<Booking> getRecentBookingsByUser(Long userId, int limit);

    /**
     * Get the count of active bookings for a specific landlord
     *
     * @param landlordId the landlord ID
     * @return number of active bookings
     */
    long getActiveBookingsCountByLandlord(Long landlordId);

    /**
     * Get the total revenue for a specific landlord
     *
     * @param landlordId the landlord ID
     * @return total revenue
     */
    BigDecimal getTotalRevenueByLandlord(Long landlordId);

    /**
     * Get recent bookings for a specific landlord
     *
     * @param landlordId the landlord ID
     * @param limit number of bookings to return
     * @return list of recent bookings
     */
    List<Booking> getRecentBookingsByLandlord(Long landlordId, int limit);

    /**
     * Get all bookings for a specific landlord
     *
     * @param landlordId the landlord ID
     * @return list of bookings
     */
    List<Booking> getBookingsByLandlord(Long landlordId);
} 
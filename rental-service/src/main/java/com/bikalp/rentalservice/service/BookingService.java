package com.bikalp.rentalservice.service;

import com.bikalp.rentalservice.entity.Booking;
import com.bikalp.rentalservice.enums.BookingStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface BookingService {
    Booking saveBooking(Booking booking);
    Optional<Booking> getBookingById(Long id);
    List<Booking> getAllBookings();
    Booking updateBooking(Long id, Booking booking);
    void deleteBooking(Long id);
    List<Booking> getBookingsByCustomerId(Long customerId);
    List<Booking> getBookingsByRoomId(Long roomId);
    List<Booking> getBookingsByStatus(BookingStatus status);
    Booking updateBookingStatus(Long id, BookingStatus status);
    List<Booking> getBookingsByLandlordId(Long landlordId);
    long getTotalBookings();
    List<Booking> getRecentBookings(int limit);
    BigDecimal getTotalRevenue();
    Booking findById(Long id);
    Booking save(Booking booking);
    void delete(Long id);
} 
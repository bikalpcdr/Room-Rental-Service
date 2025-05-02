package com.bikalp.rentalservice.service.impl;

import com.bikalp.rentalservice.entity.Booking;
import com.bikalp.rentalservice.enums.BookingStatus;
import com.bikalp.rentalservice.repository.BookingRepo;
import com.bikalp.rentalservice.service.BookingService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepo bookingRepo;

    @Override
    public Booking saveBooking(Booking booking) {
        return bookingRepo.save(booking);
    }

    @Override
    public Optional<Booking> getBookingById(Long id) {
        return bookingRepo.findById(id);
    }

    @Override
    public List<Booking> getAllBookings() {
        return bookingRepo.findAll();
    }

    @Override
    public Booking updateBooking(Long id, Booking booking) {
        Booking existingBooking = bookingRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found with id: " + id));
        
        existingBooking.setTotalPrice(booking.getTotalPrice());
        existingBooking.setSpecialRequests(booking.getSpecialRequests());
        existingBooking.setNumberOfGuests(booking.getNumberOfGuests());
        
        return bookingRepo.save(existingBooking);
    }

    @Override
    public void deleteBooking(Long id) {
        bookingRepo.deleteById(id);
    }

    @Override
    public List<Booking> getBookingsByCustomerId(Long customerId) {
        return bookingRepo.findByCustomerId(customerId);
    }

    @Override
    public List<Booking> getBookingsByRoomId(Long roomId) {
        return bookingRepo.findByRoomId(roomId);
    }

    @Override
    public List<Booking> getBookingsByStatus(BookingStatus status) {
        return bookingRepo.findByStatus(status);
    }

    @Override
    public Booking updateBookingStatus(Long id, BookingStatus status) {
        Booking booking = bookingRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found with id: " + id));
        
        booking.setStatus(status);
        return bookingRepo.save(booking);
    }

    @Override
    public List<Booking> getBookingsByLandlordId(Long landlordId) {
        return bookingRepo.findByRoomLandlordId(landlordId);
    }

    @Override
    public long getTotalBookings() {
        return bookingRepo.count();
    }

    @Override
    public List<Booking> getRecentBookings(int limit) {
        return bookingRepo.findAll(
            PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "bookingDate"))
        ).getContent();
    }

    @Override
    public BigDecimal getTotalRevenue() {
        return bookingRepo.getTotalRevenue();
    }

    @Override
    public Booking findById(Long id) {
        return bookingRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    @Override
    public Booking save(Booking booking) {
        return bookingRepo.save(booking);
    }

    @Override
    public void delete(Long id) {
        bookingRepo.deleteById(id);
    }
} 
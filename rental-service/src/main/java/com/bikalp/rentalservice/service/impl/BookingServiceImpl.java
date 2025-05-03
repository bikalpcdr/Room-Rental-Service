package com.bikalp.rentalservice.service.impl;

import com.bikalp.rentalservice.entity.Booking;
import com.bikalp.rentalservice.enums.BookingStatus;
import com.bikalp.rentalservice.repository.BookingRepo;
import com.bikalp.rentalservice.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepo bookingRepo;

    @Override
    public long getTotalBookings() {
        return bookingRepo.count();
    }

    @Override
    public List<Booking> getRecentBookings(int limit) {
        return bookingRepo.findRecentBookings(
            PageRequest.of(0, limit)
        );
    }

    @Override
    public BigDecimal getTotalRevenue() {
        return bookingRepo.getTotalRevenue();
    }

    @Override
    public Booking save(Booking booking) {
        return bookingRepo.save(booking);
    }

    @Override
    public void delete(Long id) {
        bookingRepo.deleteById(id);
    }

    @Override
    public long getActiveBookingsCount() {
        return bookingRepo.countByStatus(BookingStatus.CONFIRMED);
    }

    @Override
    public long getActiveBookingsCountByUser(Long userId) {
        return bookingRepo.countByUserIdAndStatus(userId, BookingStatus.CONFIRMED);
    }

    @Override
    public long getTotalBookingsByUser(Long userId) {
        return bookingRepo.countByUserId(userId);
    }

    @Override
    public double getTotalSpentByUser(Long userId) {
        return bookingRepo.sumTotalAmountByUserId(userId).doubleValue();
    }

    @Override
    public List<Booking> getRecentBookingsByUser(Long userId, int limit) {
        return bookingRepo.findByUserIdOrderByCreatedAtDesc(
            userId,
            PageRequest.of(0, limit)
        ).getContent();
    }
} 
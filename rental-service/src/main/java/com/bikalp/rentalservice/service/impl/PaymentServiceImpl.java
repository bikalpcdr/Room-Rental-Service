package com.bikalp.rentalservice.service.impl;

import com.bikalp.rentalservice.entity.Booking;
import com.bikalp.rentalservice.entity.Payment;
import com.bikalp.rentalservice.enums.PaymentStatus;
import com.bikalp.rentalservice.repository.BookingRepo;
import com.bikalp.rentalservice.repository.PaymentRepo;
import com.bikalp.rentalservice.service.PaymentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepo paymentRepo;
    private final BookingRepo bookingRepo;

    @Override
    public Payment savePayment(Payment payment) {
        return paymentRepo.save(payment);
    }

    @Override
    public Optional<Payment> getPaymentById(Long id) {
        return paymentRepo.findById(id);
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepo.findAll();
    }

    @Override
    public Payment updatePayment(Long id, Payment payment) {
        Payment existingPayment = paymentRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found with id: " + id));
        
        existingPayment.setAmount(payment.getAmount());
        existingPayment.setStatus(payment.getStatus());
        existingPayment.setTransactionId(payment.getTransactionId());
        existingPayment.setPaymentMethod(payment.getPaymentMethod());
        
        return paymentRepo.save(existingPayment);
    }

    @Override
    public void deletePayment(Long id) {
        paymentRepo.deleteById(id);
    }

    @Override
    public Payment processPayment(Long bookingId, String transactionId) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found with id: " + bookingId));
        
        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(booking.getTotalPrice());
        payment.setTransactionId(transactionId);
        payment.setStatus(PaymentStatus.COMPLETED);
        
        return paymentRepo.save(payment);
    }

    @Override
    public List<Payment> getPaymentsByBookingId(Long bookingId) {
        return paymentRepo.findByBookingId(bookingId);
    }

    @Override
    public List<Payment> getPaymentsByStatus(PaymentStatus status) {
        return paymentRepo.findByStatus(status);
    }

    @Override
    public Payment updatePaymentStatus(Long id, PaymentStatus status) {
        Payment payment = paymentRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found with id: " + id));
        
        payment.setStatus(status);
        return paymentRepo.save(payment);
    }

    @Override
    public List<Payment> getPaymentsByCustomerId(Long customerId) {
        return paymentRepo.findByBookingCustomerId(customerId);
    }
} 
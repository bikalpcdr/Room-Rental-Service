package com.bikalp.rentalservice.service;

import com.bikalp.rentalservice.entity.Payment;
import com.bikalp.rentalservice.enums.PaymentStatus;

import java.util.List;
import java.util.Optional;

public interface PaymentService {
    Payment savePayment(Payment payment);
    Optional<Payment> getPaymentById(Long id);
    List<Payment> getAllPayments();
    Payment updatePayment(Long id, Payment payment);
    void deletePayment(Long id);
    Payment processPayment(Long bookingId, String transactionId);
    List<Payment> getPaymentsByBookingId(Long bookingId);
    List<Payment> getPaymentsByStatus(PaymentStatus status);
    Payment updatePaymentStatus(Long id, PaymentStatus status);
    List<Payment> getPaymentsByCustomerId(Long customerId);
} 
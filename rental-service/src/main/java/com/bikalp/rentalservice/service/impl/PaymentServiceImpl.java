package com.bikalp.rentalservice.service.impl;

import com.bikalp.rentalservice.repository.BookingRepo;
import com.bikalp.rentalservice.repository.PaymentRepo;
import com.bikalp.rentalservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepo paymentRepo;
    private final BookingRepo bookingRepo;
} 
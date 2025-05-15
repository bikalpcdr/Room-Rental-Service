package com.bikalp.rentalservice.entity;

import com.bikalp.rentalservice.enums.BookingStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Booking entity class.
 * Represents bookings made by customers for rooms.
 */
@Getter
@Setter
@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;

    @NotNull
    private BigDecimal totalPrice;

    private LocalDateTime bookingDate = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private BookingStatus status = BookingStatus.PENDING;

    private String specialRequests;

    private int numberOfGuests = 1;

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL)
    private Payment payment;
}

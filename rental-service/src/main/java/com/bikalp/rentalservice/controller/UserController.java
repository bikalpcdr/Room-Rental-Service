package com.bikalp.rentalservice.controller;

import com.bikalp.rentalservice.entity.Booking;
import com.bikalp.rentalservice.entity.User;
import com.bikalp.rentalservice.service.BookingService;
import com.bikalp.rentalservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
public class UserController {

    private final UserService userService;
    private final BookingService bookingService;

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get user's booking statistics
        long activeBookings = bookingService.getActiveBookingsCountByUser(user.getId());
        long totalBookings = bookingService.getTotalBookingsByUser(user.getId());
        double totalSpent = bookingService.getTotalSpentByUser(user.getId());
        List<Booking> recentBookings = bookingService.getRecentBookingsByUser(user.getId(), 5);

        // Add data to model
        model.addAttribute("user", user);
        model.addAttribute("activeBookings", activeBookings);
        model.addAttribute("totalBookings", totalBookings);
        model.addAttribute("totalSpent", String.format("$%.2f", totalSpent));
        model.addAttribute("recentBookings", recentBookings);

        return "user/dashboard";
    }

    @GetMapping("/bookings")
    public String showBookings() {
        return "user/bookings";
    }

    @GetMapping("/profile")
    public String showProfile() {
        return "user/profile";
    }

    @GetMapping("/settings")
    public String showSettings() {
        return "user/settings";
    }
} 
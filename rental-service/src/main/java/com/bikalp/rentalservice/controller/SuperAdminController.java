package com.bikalp.rentalservice.controller;

import com.bikalp.rentalservice.entity.Booking;
import com.bikalp.rentalservice.entity.User;
import com.bikalp.rentalservice.service.BookingService;
import com.bikalp.rentalservice.service.RoomService;
import com.bikalp.rentalservice.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/superadmin")
@PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
public class SuperAdminController {

    private final UserService userService;
    private final RoomService roomService;
    private final BookingService bookingService;

    @Autowired
    public SuperAdminController(UserService userService, RoomService roomService, BookingService bookingService) {
        this.userService = userService;
        this.roomService = roomService;
        this.bookingService = bookingService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Get total counts
        long totalUsers = userService.getTotalUsers();
        long totalRooms = roomService.getTotalRooms();
        long totalBookings = bookingService.getTotalBookings();
        BigDecimal totalRevenue = bookingService.getTotalRevenue();

        // Get recent activities
        List<Booking> recentBookings = bookingService.getRecentBookings(5);
        List<User> recentUsers = userService.getRecentUsers(5);

        // Add attributes to model
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalRooms", totalRooms);
        model.addAttribute("totalBookings", totalBookings);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("recentBookings", recentBookings);
        model.addAttribute("recentUsers", recentUsers);

        return "superadmin/dashboard";
    }

    @GetMapping("/users")
    public String listUsers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String search, Model model) {

        Page<User> usersPage = userService.findAllUsers(PageRequest.of(page, size), search);

        model.addAttribute("users", usersPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", usersPage.getTotalPages());
        model.addAttribute("search", search);

        return "superadmin/users";
    }

    @GetMapping("/users/create")
    public String showCreateUserForm(Model model) {
        model.addAttribute("user", new User());
        return "superadmin/user-form";
    }

    @PostMapping("/users/create")
    public String createUser(@Valid User user, BindingResult result, Model model) {
        log.info("Creating new user: {}", user.getUsername());

        if (result.hasErrors()) {
            log.warn("Validation errors while creating user: {}", result.getAllErrors());
            // Add individual field errors
            result.getFieldErrors().forEach(error -> {
                model.addAttribute(error.getField() + "Error", error.getDefaultMessage());
            });
            model.addAttribute("user", user);
            return "superadmin/user-form";
        }

        try {
            userService.createUser(user);
            model.addAttribute("success", "User created successfully");
            return "redirect:/superadmin/users";
        } catch (Exception e) {
            log.error("Error creating user: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
            model.addAttribute("user", user);
            return "superadmin/user-form";
        }
    }

    @GetMapping("/users/{id}/edit")
    public String showEditUserForm(@PathVariable Long id, Model model) {
        log.info("Attempting to edit user with ID: {}", id);

        Optional<User> user = userService.findById(id);
        if (user.isEmpty()) {
            log.warn("User with ID {} not found", id);
            return "redirect:/superadmin/users";
        }

        log.info("Found user: {}", user.get().getUsername());
        model.addAttribute("user", user.get());
        return "superadmin/user-form";
    }

    @PostMapping("/users/{id}/edit")
    public String updateUser(@PathVariable Long id, @Valid User user, BindingResult result, Model model) {
        log.info("Updating user with ID: {}", id);

        if (result.hasErrors()) {
            log.warn("Validation errors while updating user: {}", result.getAllErrors());
            // Add individual field errors
            result.getFieldErrors().forEach(error -> {
                model.addAttribute(error.getField() + "Error", error.getDefaultMessage());
            });
            model.addAttribute("user", user);
            return "superadmin/user-form";
        }

        try {
            userService.updateUser(id, user);
            model.addAttribute("success", "User updated successfully");
            return "redirect:/superadmin/users";
        } catch (Exception e) {
            log.error("Error updating user: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
            model.addAttribute("user", user);
            return "superadmin/user-form";
        }
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {

        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("success", "User deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting user: " + e.getMessage());
        }

        return "redirect:/superadmin/users";
    }

    @PostMapping("/users/{id}/toggle-status")
    public String toggleUserStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {

        try {
            userService.toggleUserStatus(id);
            redirectAttributes.addFlashAttribute("success", "User status updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating user status: " + e.getMessage());
        }

        return "redirect:/superadmin/users";
    }

    @GetMapping("/rooms")
    public String roomsManagement() {
        return "superadmin/rooms";
    }

    @GetMapping("/bookings")
    public String bookingsManagement() {
        return "superadmin/bookings";
    }

    @GetMapping("/payments")
    public String paymentsManagement() {
        return "superadmin/payments";
    }

    @GetMapping("/settings")
    public String settings() {
        return "superadmin/settings";
    }
} 
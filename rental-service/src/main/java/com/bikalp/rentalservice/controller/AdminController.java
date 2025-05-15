package com.bikalp.rentalservice.controller;

import com.bikalp.rentalservice.entity.Booking;
import com.bikalp.rentalservice.entity.User;
import com.bikalp.rentalservice.enums.UserRole;
import com.bikalp.rentalservice.service.BookingService;
import com.bikalp.rentalservice.service.RoomService;
import com.bikalp.rentalservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final RoomService roomService;
    private final BookingService bookingService;

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        // Get total counts
        long totalUsers = userService.getTotalUsers();
        long totalRooms = roomService.getTotalRooms();
        long activeBookings = bookingService.getActiveBookingsCount();
        BigDecimal totalRevenue = bookingService.getTotalRevenue();

        // Get recent users and bookings
        List<User> recentUsers = userService.getRecentUsers(5);
        List<Booking> recentBookings = bookingService.getRecentBookings(5);

        // Add data to model
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalRooms", totalRooms);
        model.addAttribute("activeBookings", activeBookings);
        model.addAttribute("totalRevenue", String.format("$%.2f", totalRevenue));
        model.addAttribute("recentUsers", recentUsers);
        model.addAttribute("recentBookings", recentBookings);

        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String showUsers(Model model) {
        List<User> users = userService.getAllUsers().stream()
                .filter(user -> user.getRole() != UserRole.ADMIN && user.getRole() != UserRole.SUPER_ADMIN)
                .collect(Collectors.toList());
        model.addAttribute("users", users);
        return "admin/users";
    }

    @GetMapping("/rooms")
    public String showRooms() {
        return "admin/rooms";
    }

    @GetMapping("/bookings")
    public String showBookings() {
        return "admin/bookings";
    }

    @GetMapping("/settings")
    public String showSettings() {
        return "admin/settings";
    }

    @PostMapping("/users/{id}/change-to-landlord")
    @PreAuthorize("hasRole('ADMIN')")
    public String changeUserToLandlord(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.changeUserRoleToLandlord(id);
            redirectAttributes.addFlashAttribute("success", "User role changed to landlord successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error changing user role: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/users/create")
    public String showCreateUserForm(Model model) {
        User user = new User();
        user.setEnabled(true);
        model.addAttribute("user", user);
        return "admin/user-form";
    }

    @PostMapping("/users/create")
    public String createUser(@Valid @ModelAttribute("user") User user,
                             BindingResult result,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/user-form";
        }

        try {
            user.setRole(UserRole.LANDLORD);
            userService.createUser(user);
            redirectAttributes.addFlashAttribute("success", "Landlord user created successfully");
            return "redirect:/admin/users";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating user: " + e.getMessage());
            return "admin/user-form";
        }
    }

    @GetMapping("/users/{id}/edit")
    public String showEditUserForm(@PathVariable Long id, Model model) {
        User user = userService.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user", user);
        return "admin/user-form";
    }

    @PostMapping("/users/{id}/edit")
    public String updateUser(@PathVariable Long id,
                             @Valid @ModelAttribute("user") User user,
                             BindingResult result,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/user-form";
        }

        try {
            user.setRole(UserRole.LANDLORD);
            userService.updateUser(id, user);
            redirectAttributes.addFlashAttribute("success", "User updated successfully");
            return "redirect:/admin/users";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating user: " + e.getMessage());
            return "admin/user-form";
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
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/toggle-status")
    public String toggleUserStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.toggleUserStatus(id);
            redirectAttributes.addFlashAttribute("success", "User status updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating user status: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }
} 
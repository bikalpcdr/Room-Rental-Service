package com.bikalp.rentalservice.controller;

import com.bikalp.rentalservice.entity.Booking;
import com.bikalp.rentalservice.entity.Room;
import com.bikalp.rentalservice.entity.User;
import com.bikalp.rentalservice.repository.UserRepo;
import com.bikalp.rentalservice.service.BookingService;
import com.bikalp.rentalservice.service.RoomService;
import com.bikalp.rentalservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/landlord")
@RequiredArgsConstructor
@PreAuthorize("hasRole('LANDLORD')")
public class LandlordController {


    private final UserService userService;
    private final RoomService roomService;
    private final BookingService bookingService;
    private final UserRepo userRepo;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.error("User not authenticated");
            throw new RuntimeException("User not authenticated. Please log in.");
        }

        String username = authentication.getName();
        log.info("Looking for user with username: {}", username);

        User user = userService.findByUsername(username)
                .orElseGet(() -> {
                    log.warn("User not found with username: {}, trying email", username);
                    return userRepo.findByEmail(username)
                            .orElseThrow(() -> {
                                log.error("User not found with username/email: {}", username);
                                return new RuntimeException("User not found. Please try logging in again.");
                            });
                });

        log.info("Found user: {} with role: {}", user.getUsername(), user.getRole());
        return user;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        try {
            log.info("Accessing landlord dashboard");
            User landlord = getCurrentUser();
            log.info("Found landlord: {}", landlord.getUsername());

            // Get room statistics
            long totalRooms = roomService.getTotalRoomsByLandlord(landlord.getId());
            long availableRooms = roomService.getAvailableRoomsByLandlord(landlord.getId());
            log.info("Room statistics - Total: {}, Available: {}", totalRooms, availableRooms);

            // Get booking statistics
            long activeBookings = bookingService.getActiveBookingsCountByLandlord(landlord.getId());
            BigDecimal totalRevenue = bookingService.getTotalRevenueByLandlord(landlord.getId());
            log.info("Booking statistics - Active: {}, Revenue: {}", activeBookings, totalRevenue);

            // Get recent bookings and rooms
            List<Booking> recentBookings = bookingService.getRecentBookingsByLandlord(landlord.getId(), 5);
            List<Room> recentRooms = roomService.getRecentRoomsByLandlord(landlord.getId(), 5);
            log.info("Found {} recent bookings and {} recent rooms", recentBookings.size(), recentRooms.size());

            // Add data to model
            model.addAttribute("totalRooms", totalRooms);
            model.addAttribute("availableRooms", availableRooms);
            model.addAttribute("activeBookings", activeBookings);
            model.addAttribute("totalRevenue", String.format("$%.2f", totalRevenue));
            model.addAttribute("recentBookings", recentBookings);
            model.addAttribute("recentRooms", recentRooms);

            return "landlord/dashboard";
        } catch (Exception e) {
            log.error("Error accessing landlord dashboard: {}", e.getMessage(), e);
            model.addAttribute("error", e.getMessage());
            return "redirect:/auth/login";
        }
    }

    @GetMapping("/rooms")
    public String showRooms(Model model) {
        try {
            User landlord = getCurrentUser();
            List<Room> rooms = roomService.getRoomsByLandlord(landlord.getId());
            model.addAttribute("rooms", rooms);
            return "landlord/rooms";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/auth/login";
        }
    }

    @GetMapping("/rooms/create")
    public String showCreateRoomForm(Model model) {
        try {
            log.info("Accessing create room form");
            model.addAttribute("room", new Room());
            model.addAttribute("roomTypes", roomService.getAllRoomTypes());
            return "landlord/room-form";
        } catch (Exception e) {
            log.error("Error accessing create room form: {}", e.getMessage(), e);
            model.addAttribute("error", e.getMessage());
            return "redirect:/landlord/rooms";
        }
    }

    @PostMapping("/rooms/create")
    public String createRoom(@ModelAttribute Room room,
                             @RequestParam("images") List<MultipartFile> images,
                             Model model) {
        try {
            log.info("Starting room creation process");
            User currentUser = getCurrentUser();
            log.info("Setting landlord: {}", currentUser.getUsername());
            room.setLandlord(currentUser);

            log.info("Room details - Title: {}, Type: {}, Price: {}",
                    room.getTitle(), room.getRoomType(), room.getPricePerMonth());

            if (images != null) {
                log.info("Received {} images", images.size());
                for (MultipartFile image : images) {
                    log.info("Image details - Name: {}, Size: {} bytes, Content Type: {}",
                            image.getOriginalFilename(),
                            image.getSize(),
                            image.getContentType());
                }
            } else {
                log.warn("No images received in the request");
            }

            roomService.saveRoomWithImages(room, images);

            log.info("Room created successfully with ID: {}", room.getId());
            return "redirect:/landlord/rooms";
        } catch (Exception e) {
            log.error("Error creating room: {}", e.getMessage(), e);
            model.addAttribute("error", "Error creating room: " + e.getMessage());
            model.addAttribute("roomTypes", roomService.getAllRoomTypes());
            return "landlord/room-form";
        }
    }

    @GetMapping("/rooms/{id}/edit")
    public String showEditRoomForm(@PathVariable Long id, Model model) {
        try {
            log.info("Accessing edit room form for room ID: {}", id);
            Room room = roomService.getRoomById(id);
            log.info("Found room: {} with ID: {}", room.getTitle(), room.getId());
            model.addAttribute("room", room);
            model.addAttribute("roomTypes", roomService.getAllRoomTypes());
            return "landlord/room-form";
        } catch (Exception e) {
            log.error("Error accessing edit room form: {}", e.getMessage(), e);
            model.addAttribute("error", e.getMessage());
            return "redirect:/landlord/rooms";
        }
    }

    @PostMapping("/rooms/{id}/edit")
    public String updateRoom(@PathVariable Long id,
                             @ModelAttribute Room room,
                             @RequestParam(value = "images", required = false) List<MultipartFile> images,
                             @RequestParam(value = "removedImages", required = false) List<Long> removedImageIds,
                             Model model) {
        try {
            User currentUser = getCurrentUser();
            if (currentUser == null) {
                return "redirect:/auth/login";
            }

            Room existingRoom = roomService.getRoomById(id);
            if (existingRoom == null) {
                return "redirect:/landlord/rooms?error=Room not found";
            }

            // Always use the new method that handles both existing and new images
            roomService.updateRoomWithImages(id, room, images, removedImageIds);

            return "redirect:/landlord/rooms?success=Room updated successfully";
        } catch (Exception e) {
            model.addAttribute("error", "Error updating room: " + e.getMessage());
            return "redirect:/landlord/rooms?error=Error updating room";
        }
    }

    @PostMapping("/rooms/{id}/delete")
    public String deleteRoom(@PathVariable Long id) {
        try {
            roomService.deleteRoom(id);
            return "redirect:/landlord/rooms";
        } catch (Exception e) {
            return "redirect:/landlord/rooms";
        }
    }

    @GetMapping("/bookings")
    public String showBookings(Model model) {
        try {
            User landlord = getCurrentUser();
            List<Booking> bookings = bookingService.getBookingsByLandlord(landlord.getId());
            model.addAttribute("bookings", bookings);
            return "landlord/bookings";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/auth/login";
        }
    }

    @GetMapping("/profile")
    public String showProfile(Model model) {
        try {
            User landlord = getCurrentUser();
            model.addAttribute("user", landlord);
            return "landlord/profile";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/auth/login";
        }
    }
} 
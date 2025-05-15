package com.bikalp.rentalservice.controller;

import com.bikalp.rentalservice.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@PreAuthorize("hasRole('LANDLORD')")
public class RoomController {

    private final RoomService roomService;

    @PostMapping("/{id}/toggle")
    public String toggleRoomAvailability(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            roomService.toggleRoomAvailability(id);
            redirectAttributes.addFlashAttribute("success", "Room availability updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update room availability");
        }
        return "redirect:/landlord/rooms";
    }
} 
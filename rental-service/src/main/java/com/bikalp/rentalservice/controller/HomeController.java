package com.bikalp.rentalservice.controller;

import com.bikalp.rentalservice.entity.Room;
import com.bikalp.rentalservice.entity.RoomImage;
import com.bikalp.rentalservice.enums.RoomType;
import com.bikalp.rentalservice.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Base64;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final RoomService roomService;

    @GetMapping("/")
    public String home(Model model) {
        // Get featured rooms
        List<Room> featuredRooms = roomService.getFeaturedRooms();
        model.addAttribute("featuredRooms", featuredRooms);

        // Get recent rooms
        List<Room> recentRooms = roomService.getRecentRooms();
        model.addAttribute("recentRooms", recentRooms);

        // Get available rooms (first page)
        Page<Room> availableRooms = roomService.getAvailableRooms(
            PageRequest.of(0, 8, Sort.by("createdAt").descending())
        );
        model.addAttribute("availableRooms", availableRooms);

        // Add room types for filter
        model.addAttribute("roomTypes", RoomType.values());

        // Add utility methods
        model.addAttribute("imageUtil", new ImageUtil());

        return "home";
    }

    @GetMapping("/search")
    public String search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) RoomType roomType,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            Model model) {

        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        PageRequest pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<Room> searchResults = roomService.searchRooms(keyword, roomType, minPrice, maxPrice, pageable);

        model.addAttribute("searchResults", searchResults);
        model.addAttribute("keyword", keyword);
        model.addAttribute("roomType", roomType);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("roomTypes", RoomType.values());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", searchResults.getTotalPages());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);
        model.addAttribute("imageUtil", new ImageUtil());

        return "search-results";
    }

    @GetMapping("/room/{id}")
    public String roomDetails(@PathVariable Long id, Model model) {
        roomService.getRoomById(id).ifPresent(room -> {
            model.addAttribute("room", room);
            model.addAttribute("relatedRooms", roomService.getRoomsByType(
                room.getRoomType(),
                PageRequest.of(0, 4, Sort.by("createdAt").descending())
            ));
            model.addAttribute("imageUtil", new ImageUtil());
        });
        return "room-details";
    }

    public static class ImageUtil {
        public String getImageUrl(Room room) {
            if (room != null && room.getRoomImages() != null && !room.getRoomImages().isEmpty()) {
                RoomImage image = room.getRoomImages().get(0);
                if (image != null && image.getData() != null) {
                    return "data:" + image.getContentType() + ";base64," + 
                           Base64.getEncoder().encodeToString(image.getData());
                }
            }
            return "/images/default-room.jpg";
        }
    }
} 
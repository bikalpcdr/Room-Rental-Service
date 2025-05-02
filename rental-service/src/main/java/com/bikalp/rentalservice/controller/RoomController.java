package com.bikalp.rentalservice.controller;

import com.bikalp.rentalservice.entity.Room;
import com.bikalp.rentalservice.enums.RoomType;
import com.bikalp.rentalservice.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<Room> createRoom(@Valid @RequestBody Room room) {
        return ResponseEntity.ok(roomService.saveRoom(room));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Room> getRoomById(@PathVariable Long id) {
        return roomService.getRoomById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Page<Room>> getAllRooms(Pageable pageable) {
        return ResponseEntity.ok(roomService.getAllRooms(pageable));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<Room> updateRoom(@PathVariable Long id, @Valid @RequestBody Room room) {
        return ResponseEntity.ok(roomService.updateRoom(id, room));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/type/{roomType}")
    public ResponseEntity<Page<Room>> getRoomsByType(
            @PathVariable RoomType roomType,
            Pageable pageable) {
        return ResponseEntity.ok(roomService.getRoomsByType(roomType, pageable));
    }

    @GetMapping("/available")
    public ResponseEntity<Page<Room>> getAvailableRooms(Pageable pageable) {
        return ResponseEntity.ok(roomService.getAvailableRooms(pageable));
    }

    @GetMapping("/landlord/{landlordId}")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<Page<Room>> getRoomsByLandlordId(
            @PathVariable Long landlordId,
            Pageable pageable) {
        return ResponseEntity.ok(roomService.getRoomsByLandlordId(landlordId, pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Room>> searchRooms(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) RoomType roomType,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            Pageable pageable) {
        return ResponseEntity.ok(roomService.searchRooms(keyword, roomType, minPrice, maxPrice, pageable));
    }

    @PutMapping("/{id}/availability")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<Room> updateRoomAvailability(
            @PathVariable Long id,
            @RequestParam boolean available) {
        return ResponseEntity.ok(roomService.updateRoomAvailability(id, available));
    }

    @GetMapping("/featured")
    public ResponseEntity<List<Room>> getFeaturedRooms() {
        return ResponseEntity.ok(roomService.getFeaturedRooms());
    }

    @GetMapping("/recent")
    public ResponseEntity<List<Room>> getRecentRooms() {
        return ResponseEntity.ok(roomService.getRecentRooms());
    }
} 
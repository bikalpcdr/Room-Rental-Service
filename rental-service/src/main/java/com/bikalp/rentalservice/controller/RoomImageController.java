package com.bikalp.rentalservice.controller;

import com.bikalp.rentalservice.entity.RoomImage;
import com.bikalp.rentalservice.service.RoomImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/room-images")
@RequiredArgsConstructor
public class RoomImageController {

    private final RoomImageService roomImageService;

    @PostMapping
    public ResponseEntity<RoomImage> createRoomImage(@RequestBody RoomImage roomImage) {
        return ResponseEntity.ok(roomImageService.saveRoomImage(roomImage));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomImage> getRoomImageById(@PathVariable Long id) {
        return roomImageService.getRoomImageById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<RoomImage>> getAllRoomImages() {
        return ResponseEntity.ok(roomImageService.getAllRoomImages());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoomImage(@PathVariable Long id) {
        roomImageService.deleteRoomImage(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<RoomImage>> getImagesByRoomId(@PathVariable Long roomId) {
        return ResponseEntity.ok(roomImageService.getImagesByRoomId(roomId));
    }

    @PostMapping(value = "/upload/{roomId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RoomImage> uploadRoomImage(
            @PathVariable Long roomId,
            @RequestParam("file") MultipartFile file) {
        try {
            return ResponseEntity.ok(roomImageService.uploadRoomImage(roomId, file));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/room/{roomId}")
    public ResponseEntity<Void> deleteAllImagesByRoomId(@PathVariable Long roomId) {
        roomImageService.deleteAllImagesByRoomId(roomId);
        return ResponseEntity.ok().build();
    }
} 
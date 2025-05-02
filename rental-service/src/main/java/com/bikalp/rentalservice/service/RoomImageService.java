package com.bikalp.rentalservice.service;

import com.bikalp.rentalservice.entity.RoomImage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface RoomImageService {
    RoomImage saveRoomImage(RoomImage roomImage);
    Optional<RoomImage> getRoomImageById(Long id);
    List<RoomImage> getAllRoomImages();
    void deleteRoomImage(Long id);
    List<RoomImage> getImagesByRoomId(Long roomId);
    RoomImage uploadRoomImage(Long roomId, MultipartFile file);
    void deleteAllImagesByRoomId(Long roomId);
} 
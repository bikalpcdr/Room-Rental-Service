package com.bikalp.rentalservice.service.impl;

import com.bikalp.rentalservice.entity.Room;
import com.bikalp.rentalservice.entity.RoomImage;
import com.bikalp.rentalservice.enums.RoomType;
import com.bikalp.rentalservice.repository.RoomRepo;
import com.bikalp.rentalservice.service.RoomService;
import com.bikalp.rentalservice.util.ImageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepo roomRepo;
    private static final Logger log = LoggerFactory.getLogger(RoomServiceImpl.class);

    @Override
    @Transactional
    public void saveRoom(Room room) {
        roomRepo.save(room);
    }

    @Override
    @Transactional
    public void updateRoom(Long id, Room room) {
        Room existingRoom = getRoomById(id);
        room.setId(id);
        room.setLandlord(existingRoom.getLandlord());
        roomRepo.save(room);
    }

    @Override
    @Transactional
    public void deleteRoom(Long id) {
        roomRepo.deleteById(id);
    }

    @Override
    public Room getRoomById(Long id) {
        return roomRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + id));
    }

    @Override
    public List<Room> getRoomsByLandlord(Long landlordId) {
        return roomRepo.findByLandlordId(landlordId);
    }

    @Override
    public long getTotalRoomsByLandlord(Long landlordId) {
        return roomRepo.findByLandlordId(landlordId).size();
    }

    @Override
    public long getAvailableRoomsByLandlord(Long landlordId) {
        return roomRepo.findByLandlordId(landlordId).stream()
                .filter(Room::isAvailable)
                .count();
    }

    @Override
    public List<Room> getRecentRoomsByLandlord(Long landlordId, int limit) {
        return roomRepo.findByLandlordId(landlordId).stream()
                .sorted((r1, r2) -> r2.getCreatedAt().compareTo(r1.getCreatedAt()))
                .limit(limit)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<Room> getAllRooms() {
        return roomRepo.findAll();
    }

    @Override
    public List<Room> getAvailableRooms() {
        return roomRepo.findByAvailableTrue();
    }

    @Override
    public List<Room> getRoomsByType(String roomType) {
        return roomRepo.findByRoomType(RoomType.valueOf(roomType));
    }

    @Override
    public List<Room> getRoomsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return roomRepo.searchRooms(null, null, minPrice.doubleValue(), maxPrice.doubleValue(), Pageable.unpaged())
                .getContent();
    }

    @Override
    public List<Room> searchRooms(String keyword) {
        return roomRepo.searchRooms(keyword);
    }

    @Override
    public List<Room> getFeaturedRooms() {
        return roomRepo.findTop5ByOrderByCreatedAtDesc();
    }

    @Override
    public List<Room> getRecentRooms(int limit) {
        return roomRepo.findTop10ByOrderByCreatedAtDesc();
    }

    @Override
    public Page<Room> getRoomsByType(RoomType roomType, Pageable pageable) {
        return roomRepo.findByRoomType(roomType, pageable);
    }

    @Override
    public Page<Room> getAvailableRooms(Pageable pageable) {
        return roomRepo.findByAvailableTrue(pageable);
    }

    @Override
    public Page<Room> searchRooms(String keyword, RoomType roomType, Double minPrice, Double maxPrice, Pageable pageable) {
        return roomRepo.searchRooms(keyword, roomType, minPrice, maxPrice, pageable);
    }

    @Override
    public long getTotalRooms() {
        return roomRepo.count();
    }

    @Override
    public List<RoomType> getAllRoomTypes() {
        return List.of(RoomType.values());
    }

    @Override
    @Transactional
    public void saveRoomWithImages(Room room, List<MultipartFile> images) {
        log.info("Saving room with {} images", images != null ? images.size() : 0);
        
        if (images != null && !images.isEmpty()) {
            try {
                log.info("Converting images to Base64");
                List<String> base64Images = ImageUtil.convertMultipleToBase64(images);
                log.info("Successfully converted {} images to Base64", base64Images.size());
                
                List<RoomImage> roomImages = base64Images.stream()
                        .map(base64Image -> {
                            RoomImage roomImage = new RoomImage();
                            roomImage.setImageData(base64Image);
                            roomImage.setRoom(room);
                            // Set the first image as primary
                            roomImage.setPrimary(base64Images.indexOf(base64Image) == 0);
                            log.info("Created room image, primary: {}", roomImage.isPrimary());
                            return roomImage;
                        })
                        .toList();
                log.info("Setting {} room images to room", roomImages.size());
                room.setRoomImages(roomImages);
                
                // Log room state before saving
                log.info("Room state before save - ID: {}, Title: {}, Images count: {}", 
                        room.getId(), room.getTitle(), 
                        room.getRoomImages() != null ? room.getRoomImages().size() : 0);
            } catch (IOException e) {
                log.error("Error processing images: {}", e.getMessage(), e);
                throw new RuntimeException("Error processing images", e);
            }
        }
        log.info("Saving room to database");
        roomRepo.save(room);
        log.info("Room saved successfully with ID: {}", room.getId());
        
        // Verify the saved room
        Room savedRoom = roomRepo.findById(room.getId()).orElse(null);
        if (savedRoom != null) {
            log.info("Verification - Saved room has {} images", 
                    savedRoom.getRoomImages() != null ? savedRoom.getRoomImages().size() : 0);
        } else {
            log.error("Room not found after saving!");
        }
    }

    @Override
    @Transactional
    public void updateRoomWithImages(Long id, Room room, List<MultipartFile> images) {
        Room existingRoom = getRoomById(id);
        room.setId(id);
        room.setLandlord(existingRoom.getLandlord());

        if (images != null && !images.isEmpty()) {
            try {
                List<String> base64Images = ImageUtil.convertMultipleToBase64(images);
                List<RoomImage> roomImages = base64Images.stream()
                        .map(base64Image -> {
                            RoomImage roomImage = new RoomImage();
                            roomImage.setImageData(base64Image);
                            roomImage.setRoom(room);
                            // Set the first image as primary
                            roomImage.setPrimary(base64Images.indexOf(base64Image) == 0);
                            return roomImage;
                        })
                        .toList();
                room.setRoomImages(roomImages);
            } catch (IOException e) {
                throw new RuntimeException("Error processing images", e);
            }
        }
        roomRepo.save(room);
    }
} 
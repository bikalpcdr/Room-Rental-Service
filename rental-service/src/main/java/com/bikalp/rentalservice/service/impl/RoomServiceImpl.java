package com.bikalp.rentalservice.service.impl;

import com.bikalp.rentalservice.entity.Room;
import com.bikalp.rentalservice.entity.RoomImage;
import com.bikalp.rentalservice.enums.RoomType;
import com.bikalp.rentalservice.repository.RoomImageRepo;
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
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepo roomRepo;
    private final RoomImageRepo roomImageRepository;
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
                
                List<RoomImage> roomImages = new ArrayList<>();
                for (int i = 0; i < base64Images.size(); i++) {
                    RoomImage roomImage = new RoomImage();
                    roomImage.setImageData(base64Images.get(i));
                    roomImage.setRoom(room);
                    // Set only the first image as primary
                    roomImage.setPrimary(i == 0);
                    log.info("Created room image, primary: {}", roomImage.isPrimary());
                    roomImages.add(roomImage);
                }
                
                log.info("Setting {} room images to room", roomImages.size());
                room.setRoomImages(roomImages);
                
                // Log room state before save
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
            if (savedRoom.getRoomImages() != null) {
                savedRoom.getRoomImages().forEach(img -> 
                    log.info("Image ID: {}, Primary: {}", img.getId(), img.isPrimary()));
            }
        } else {
            log.error("Room not found after saving!");
        }
    }

    @Override
    @Transactional
    public void updateRoomWithImages(Long id, Room room, List<MultipartFile> images, List<Long> removedImageIds) {
        Room existingRoom = getRoomById(id);
        room.setId(id);
        room.setLandlord(existingRoom.getLandlord());

        // 1. Start with all existing images except those marked for removal
        List<RoomImage> finalImages = new ArrayList<>();
        if (existingRoom.getRoomImages() != null) {
            for (RoomImage img : existingRoom.getRoomImages()) {
                if (removedImageIds == null || !removedImageIds.contains(img.getId())) {
                    finalImages.add(img);
                } else {
                    // Remove from DB
                    roomImageRepository.deleteById(img.getId());
                }
            }
        }

        // 2. Add new images
        if (images != null && !images.isEmpty()) {
            try {
                List<String> base64Images = ImageUtil.convertMultipleToBase64(images);
                for (String base64 : base64Images) {
                    RoomImage roomImage = new RoomImage();
                    roomImage.setImageData(base64);
                    roomImage.setRoom(room);
                    roomImage.setPrimary(false);
                    finalImages.add(roomImage);
                }
            } catch (IOException e) {
                throw new RuntimeException("Error processing images", e);
            }
        }

        // Debug logging
        log.info("Final images to save (count={}):", finalImages.size());
        for (RoomImage img : finalImages) {
            log.info("Image ID: {}, isNew: {}", img.getId(), img.getId() == null);
        }

        // 3. Save the combined list
        room.setRoomImages(finalImages);
        roomRepo.save(room);
    }

    @Override
    @Transactional
    public void removeImages(List<Long> imageIds) {
        log.info("Removing images with IDs: {}", imageIds);
        for (Long imageId : imageIds) {
            try {
                RoomImage image = roomImageRepository.findById(imageId)
                    .orElseThrow(() -> new RuntimeException("Image not found with id: " + imageId));
                
                // Get the room and remove the image from its list
                Room room = image.getRoom();
                if (room != null) {
                    List<RoomImage> roomImages = room.getRoomImages();
                    if (roomImages != null) {
                        roomImages.removeIf(img -> img.getId().equals(imageId));
                        room.setRoomImages(roomImages);
                        roomRepo.save(room);
                    }
                }
                
                // Delete the image
                roomImageRepository.deleteById(imageId);
                log.info("Removed image with ID: {}", imageId);
            } catch (Exception e) {
                log.error("Error removing image with ID {}: {}", imageId, e.getMessage());
                throw new RuntimeException("Error removing image: " + e.getMessage());
            }
        }
    }
} 
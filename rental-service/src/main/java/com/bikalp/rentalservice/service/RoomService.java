package com.bikalp.rentalservice.service;

import com.bikalp.rentalservice.entity.Room;
import com.bikalp.rentalservice.enums.RoomType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

public interface RoomService {
    // Basic CRUD operations
    void saveRoom(Room room);
    void updateRoom(Long id, Room room);
    void deleteRoom(Long id);
    Room getRoomById(Long id);

    // Landlord-specific operations
    List<Room> getRoomsByLandlord(Long landlordId);
    long getTotalRoomsByLandlord(Long landlordId);
    long getAvailableRoomsByLandlord(Long landlordId);
    List<Room> getRecentRoomsByLandlord(Long landlordId, int limit);

    // General room operations
    List<Room> getAllRooms();
    List<Room> getAvailableRooms();
    List<Room> getRoomsByType(String roomType);
    List<Room> getRoomsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);
    List<Room> searchRooms(String keyword);
    List<Room> getFeaturedRooms();
    List<Room> getRecentRooms(int limit);

    Page<Room> getRoomsByType(RoomType roomType, Pageable pageable);
    Page<Room> getAvailableRooms(Pageable pageable);
    Page<Room> searchRooms(String keyword, RoomType roomType, Double minPrice, Double maxPrice, Pageable pageable);
    long getTotalRooms();

    /**
     * Get all available room types
     * @return List of all room types
     */
    List<RoomType> getAllRoomTypes();

    /**
     * Save room with multiple images
     * @param room The room to save
     * @param images List of image files
     */
    void saveRoomWithImages(Room room, List<MultipartFile> images);

    /**
     * Update room with multiple images
     * @param id Room ID
     * @param room The room to update
     * @param images List of image files
     */
    void updateRoomWithImages(Long id, Room room, List<MultipartFile> images);
} 
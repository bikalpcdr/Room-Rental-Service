package com.bikalp.rentalservice.service;

import com.bikalp.rentalservice.entity.Room;
import com.bikalp.rentalservice.enums.RoomType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface RoomService {
    Room saveRoom(Room room);
    Optional<Room> getRoomById(Long id);
    Page<Room> getAllRooms(Pageable pageable);
    Room updateRoom(Long id, Room room);
    void deleteRoom(Long id);
    Page<Room> getRoomsByType(RoomType roomType, Pageable pageable);
    Page<Room> getAvailableRooms(Pageable pageable);
    Page<Room> getRoomsByLandlordId(Long landlordId, Pageable pageable);
    Page<Room> searchRooms(String keyword, RoomType roomType, Double minPrice, Double maxPrice, Pageable pageable);
    Room updateRoomAvailability(Long id, boolean available);
    List<Room> getFeaturedRooms();
    List<Room> getRecentRooms();
    long getTotalRooms();
} 
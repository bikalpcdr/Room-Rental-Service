package com.bikalp.rentalservice.service;

import com.bikalp.rentalservice.entity.Room;
import com.bikalp.rentalservice.enums.RoomType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface RoomService {
    Optional<Room> getRoomById(Long id);
    Page<Room> getRoomsByType(RoomType roomType, Pageable pageable);
    Page<Room> getAvailableRooms(Pageable pageable);
    Page<Room> searchRooms(String keyword, RoomType roomType, Double minPrice, Double maxPrice, Pageable pageable);
    List<Room> getFeaturedRooms();
    List<Room> getRecentRooms();
    long getTotalRooms();
} 
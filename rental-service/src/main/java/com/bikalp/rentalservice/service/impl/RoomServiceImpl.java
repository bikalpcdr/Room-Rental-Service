package com.bikalp.rentalservice.service.impl;

import com.bikalp.rentalservice.entity.Room;
import com.bikalp.rentalservice.enums.RoomType;
import com.bikalp.rentalservice.repository.RoomRepo;
import com.bikalp.rentalservice.service.RoomService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomServiceImpl implements RoomService {

    private final RoomRepo roomRepo;

    @Override
    public Room saveRoom(Room room) {
        return roomRepo.save(room);
    }

    @Override
    public Optional<Room> getRoomById(Long id) {
        return roomRepo.findById(id);
    }

    @Override
    public Page<Room> getAllRooms(Pageable pageable) {
        return roomRepo.findAll(pageable);
    }

    @Override
    public Room updateRoom(Long id, Room room) {
        Room existingRoom = roomRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Room not found with id: " + id));
        
        existingRoom.setTitle(room.getTitle());
        existingRoom.setDescription(room.getDescription());
        existingRoom.setPricePerMonth(room.getPricePerMonth());
        existingRoom.setRoomType(room.getRoomType());
        existingRoom.setAvailable(room.isAvailable());
        existingRoom.setAddress(room.getAddress());
        existingRoom.setLandlord(room.getLandlord());
        
        return roomRepo.save(existingRoom);
    }

    @Override
    public void deleteRoom(Long id) {
        roomRepo.deleteById(id);
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
    public Page<Room> getRoomsByLandlordId(Long landlordId, Pageable pageable) {
        return roomRepo.findByLandlordId(landlordId, pageable);
    }

    @Override
    public Page<Room> searchRooms(String keyword, RoomType roomType, Double minPrice, Double maxPrice, Pageable pageable) {
        if (keyword != null && !keyword.isEmpty()) {
            return roomRepo.searchRooms(keyword, roomType, minPrice, maxPrice, pageable);
        }
        return roomRepo.findByFilters(roomType, minPrice, maxPrice, pageable);
    }

    @Override
    public Room updateRoomAvailability(Long id, boolean available) {
        Room room = roomRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Room not found with id: " + id));
        room.setAvailable(available);
        return roomRepo.save(room);
    }

    @Override
    public List<Room> getFeaturedRooms() {
        return roomRepo.findTop5ByOrderByCreatedAtDesc();
    }

    @Override
    public List<Room> getRecentRooms() {
        return roomRepo.findTop10ByOrderByCreatedAtDesc();
    }
} 
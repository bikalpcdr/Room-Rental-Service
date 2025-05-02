package com.bikalp.rentalservice.service.impl;

import com.bikalp.rentalservice.entity.Room;
import com.bikalp.rentalservice.entity.RoomImage;
import com.bikalp.rentalservice.repository.RoomImageRepo;
import com.bikalp.rentalservice.repository.RoomRepo;
import com.bikalp.rentalservice.service.RoomImageService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomImageServiceImpl implements RoomImageService {

    private final RoomImageRepo roomImageRepo;
    private final RoomRepo roomRepo;

    @Override
    public RoomImage saveRoomImage(RoomImage roomImage) {
        return roomImageRepo.save(roomImage);
    }

    @Override
    public Optional<RoomImage> getRoomImageById(Long id) {
        return roomImageRepo.findById(id);
    }

    @Override
    public List<RoomImage> getAllRoomImages() {
        return roomImageRepo.findAll();
    }

    @Override
    public void deleteRoomImage(Long id) {
        roomImageRepo.deleteById(id);
    }

    @Override
    public List<RoomImage> getImagesByRoomId(Long roomId) {
        return roomImageRepo.findByRoomId(roomId);
    }

    @Override
    public RoomImage uploadRoomImage(Long roomId, MultipartFile file) {
        Room room = roomRepo.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Room not found with id: " + roomId));

        RoomImage roomImage = new RoomImage();
        roomImage.setName(file.getOriginalFilename());
        roomImage.setContentType(file.getContentType());
        try {
            roomImage.setData(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        roomImage.setRoom(room);

        return roomImageRepo.save(roomImage);
    }

    @Override
    public void deleteAllImagesByRoomId(Long roomId) {
        roomImageRepo.deleteByRoomId(roomId);
    }
} 
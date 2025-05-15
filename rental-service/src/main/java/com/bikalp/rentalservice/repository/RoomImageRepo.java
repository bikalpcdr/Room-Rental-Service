package com.bikalp.rentalservice.repository;

import com.bikalp.rentalservice.entity.RoomImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomImageRepo extends JpaRepository<RoomImage, Long> {
    List<RoomImage> findByRoomId(Long roomId);

    @Query(value = "SELECT * FROM room_images ri " +
            "JOIN rooms r ON ri.room_id = r.id " +
            "WHERE r.available = true AND r.id = :roomId",
            nativeQuery = true)
    List<RoomImage> findImagesByAvailableRoom(@Param("roomId") Long roomId);

    @Query(value = "DELETE FROM room_images WHERE room_id = :roomId",
            nativeQuery = true)
    void deleteByRoomId(@Param("roomId") Long roomId);

    @Query(value = "SELECT * FROM room_images ri " +
            "JOIN rooms r ON ri.room_id = r.id " +
            "WHERE r.landlord_id = :landlordId",
            nativeQuery = true)
    List<RoomImage> findByLandlordId(@Param("landlordId") Long landlordId);
}
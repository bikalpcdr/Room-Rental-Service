package com.bikalp.rentalservice.repository;

import com.bikalp.rentalservice.entity.Room;
import com.bikalp.rentalservice.enums.RoomType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Room entity.
 * Provides data access operations for rooms.
 */
@Repository
public interface RoomRepo extends JpaRepository<Room, Long> {
    Page<Room> findByRoomType(RoomType roomType, Pageable pageable);

    Page<Room> findByAvailableTrue(Pageable pageable);

    @Query("SELECT r FROM Room r WHERE " +
           "(:keyword IS NULL OR LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:roomType IS NULL OR r.roomType = :roomType) AND " +
           "(:minPrice IS NULL OR r.pricePerMonth >= :minPrice) AND " +
           "(:maxPrice IS NULL OR r.pricePerMonth <= :maxPrice) AND " +
           "r.available = true")
    Page<Room> searchRooms(
            @Param("keyword") String keyword,
            @Param("roomType") RoomType roomType,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            Pageable pageable
    );

    // for features rooms
    @Query(value = "select * from rooms where available = true order by created_at desc limit 6", nativeQuery = true)
    List<Room> getFeaturedRooms();

    // for recently added rooms
    @Query(value = "select * from rooms where available = true order by created_at desc LIMIT 8", nativeQuery = true)
    List<Room> getRecentRooms();

    List<Room> findByLandlordId(Long landlordId);
}

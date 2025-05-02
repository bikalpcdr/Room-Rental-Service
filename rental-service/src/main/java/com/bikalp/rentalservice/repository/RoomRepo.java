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

    Page<Room> findByLandlordId(Long landlordId, Pageable pageable);

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

    @Query("SELECT r FROM Room r WHERE " +
           "(:roomType IS NULL OR r.roomType = :roomType) AND " +
           "(:minPrice IS NULL OR r.pricePerMonth >= :minPrice) AND " +
           "(:maxPrice IS NULL OR r.pricePerMonth <= :maxPrice) AND " +
           "r.available = true")
    Page<Room> findByFilters(
            @Param("roomType") RoomType roomType,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            Pageable pageable);

    List<Room> findTop5ByOrderByCreatedAtDesc();

    List<Room> findTop10ByOrderByCreatedAtDesc();

    List<Room> findByRoomType(RoomType roomType);
    List<Room> findByLandlordId(Long landlordId);
    List<Room> findByAvailableTrue();
    
    @Query("SELECT r FROM Room r WHERE r.available = true AND r.roomType = :roomType")
    List<Room> findAvailableRoomsByType(@Param("roomType") RoomType roomType);
    
    @Query("SELECT COUNT(r) FROM Room r WHERE r.available = true")
    long countAvailableRooms();
    
    @Query("SELECT COUNT(r) FROM Room r WHERE r.roomType = :roomType")
    long countRoomsByType(@Param("roomType") RoomType roomType);
    
    @Query("SELECT r FROM Room r ORDER BY r.createdAt DESC")
    List<Room> findRecentRooms(Pageable pageable);
    
    @Query("SELECT r FROM Room r WHERE r.title LIKE %:keyword% OR r.description LIKE %:keyword%")
    List<Room> searchRooms(@Param("keyword") String keyword);
}

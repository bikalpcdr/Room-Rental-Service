package com.bikalp.rentalservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "room_images")
public class RoomImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String imageData; // Store Base64 encoded image

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    private boolean isPrimary = false;
}

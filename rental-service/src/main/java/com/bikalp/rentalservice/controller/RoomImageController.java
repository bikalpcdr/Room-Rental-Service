package com.bikalp.rentalservice.controller;

import com.bikalp.rentalservice.service.RoomImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/room-images")
@RequiredArgsConstructor
public class RoomImageController {

    private final RoomImageService roomImageService;

} 
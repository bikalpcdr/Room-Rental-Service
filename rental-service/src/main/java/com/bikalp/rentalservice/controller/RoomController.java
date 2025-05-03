package com.bikalp.rentalservice.controller;

import com.bikalp.rentalservice.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@PreAuthorize("hasRole('LANDLORD')")
public class RoomController {

    private final RoomService roomService;
} 
package com.bikalp.rentalservice.service.impl;

import com.bikalp.rentalservice.repository.RoomImageRepo;
import com.bikalp.rentalservice.repository.RoomRepo;
import com.bikalp.rentalservice.service.RoomImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomImageServiceImpl implements RoomImageService {

    private final RoomImageRepo roomImageRepo;
    private final RoomRepo roomRepo;
} 
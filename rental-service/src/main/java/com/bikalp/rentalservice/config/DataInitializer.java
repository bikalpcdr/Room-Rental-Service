package com.bikalp.rentalservice.config;

import com.bikalp.rentalservice.entity.Room;
import com.bikalp.rentalservice.entity.User;
import com.bikalp.rentalservice.enums.RoomType;
import com.bikalp.rentalservice.enums.UserRole;
import com.bikalp.rentalservice.repository.RoomRepo;
import com.bikalp.rentalservice.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepo userRepository;
    private final RoomRepo roomRepo;
    private final PasswordEncoder passwordEncoder;
    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Override
    public void run(String... args) {
        // Create superadmin user if not exists
        if (!userRepository.existsByEmail("superadmin@rentalservice.com")) {
            log.info("Creating superadmin user...");
            User superadmin = new User();
            superadmin.setUsername("superadmin");
            superadmin.setFullName("Super Admin");
            superadmin.setEmail("superadmin@rentalservice.com");
            superadmin.setPassword(passwordEncoder.encode("SuperAdmin@123"));
            superadmin.setPhoneNumber("+1234567890");
            superadmin.setRole(UserRole.SUPER_ADMIN);
            superadmin.setEnabled(true);
            userRepository.save(superadmin);
            log.info("Superadmin user created successfully!");
        } else {
            log.info("Superadmin user already exists");
        }

        // Create sample landlord
        if (!userRepository.existsByEmail("landlord@rentalservice.com")) {
            User landlord = new User();
            landlord.setUsername("landlord");
            landlord.setFullName("John Landlord");
            landlord.setEmail("landlord@rentalservice.com");
            landlord.setPassword(passwordEncoder.encode("Landlord@123"));
            landlord.setPhoneNumber("+1234567891");
            landlord.setRole(UserRole.LANDLORD);
            landlord.setEnabled(true);
            userRepository.save(landlord);
            System.out.println("Landlord user created successfully!");
        }

        // Create sample rooms if none exist
        if (roomRepo.count() == 0) {
            User landlord = userRepository.findByEmail("landlord@rentalservice.com")
                    .orElseThrow(() -> new RuntimeException("Landlord not found"));

            List<Room> sampleRooms = Arrays.asList(
                createRoom("Luxury Apartment", "Beautiful apartment in the city center", 
                        1500.00, "123 Main St", 2, 4, RoomType.APARTMENT, landlord),
                createRoom("Cozy Studio", "Perfect for singles or couples", 
                        800.00, "456 Park Ave", 1, 2, RoomType.PRIVATE_ROOM, landlord),
                createRoom("Modern House", "Spacious house with garden", 
                        2500.00, "789 Oak St", 4, 6, RoomType.HOUSE, landlord),
                createRoom("Downtown Office", "Professional office space", 
                        1200.00, "321 Business Ave", 1, 2, RoomType.OFFICE, landlord),
                createRoom("Shared Room", "Great for students", 
                        500.00, "654 College St", 1, 2, RoomType.SHARED_ROOM, landlord)
            );

            roomRepo.saveAll(sampleRooms);
            System.out.println("Sample rooms created successfully!");
        }
    }

    private Room createRoom(String title, String description, double price, 
                          String address, double numberOfRooms, int capacity,
                          RoomType roomType, User landlord) {
        Room room = new Room();
        room.setTitle(title);
        room.setDescription(description);
        room.setPricePerMonth(BigDecimal.valueOf(price));
        room.setAddress(address);
        room.setNumberOfRooms(numberOfRooms);
        room.setCapacity(capacity);
        room.setRoomType(roomType);
        room.setLandlord(landlord);
        room.setAvailable(true);
        room.setAmenities("WiFi,Air Conditioning,Kitchen,Parking");
        return room;
    }
} 
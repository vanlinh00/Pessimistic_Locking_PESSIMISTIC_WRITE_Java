package com.hospital.booking;

import com.hospital.booking.entity.CheckupSlot;
import com.hospital.booking.repository.CheckupSlotRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BookingApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookingApplication.class, args);
    }

    @Bean
    CommandLineRunner initDemoSlot(CheckupSlotRepository repo) {
        return args -> {
            if (repo.count() == 0) {
                repo.save(new CheckupSlot("Dr. Lan - 9:00 AM", 1));
            }
        };
    }
}

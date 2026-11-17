package com.hospital.booking.controller;

import com.hospital.booking.dto.BookingRequest;
import com.hospital.booking.service.BookingService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/slots/{slotId}/book")
    public ResponseEntity<Map<String, String>> bookCheckup(
            @PathVariable Long slotId,
            @Valid @RequestBody BookingRequest request) {
        String message = bookingService.bookCheckup(slotId, request.patientName().trim());
        return ResponseEntity.ok(Map.of("message", message));
    }
}

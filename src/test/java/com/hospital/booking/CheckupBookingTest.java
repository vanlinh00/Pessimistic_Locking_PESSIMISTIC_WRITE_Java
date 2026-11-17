package com.hospital.booking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hospital.booking.dto.BookingRequest;
import com.hospital.booking.entity.CheckupSlot;
import com.hospital.booking.exception.SlotFullException;
import com.hospital.booking.repository.CheckupSlotRepository;
import com.hospital.booking.service.BookingService;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CheckupBookingTest {

    @Autowired
    private BookingService service;

    @Autowired
    private CheckupSlotRepository repo;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void twoPatientsBookingLastSlot_OneSucceedsOneGetsFullMessage() throws Exception {
        // Dr. Lan has only 1 checkup slot left
        CheckupSlot slot = repo.save(new CheckupSlot("Dr. Lan - 9:00 AM", 1));

        ExecutorService pool = Executors.newFixedThreadPool(2);
        CountDownLatch startGate = new CountDownLatch(1);

        try {
            // Both patients click "Book" at the exact same millisecond
            Future<String> patientA = pool.submit(() -> {
                startGate.await();
                return service.bookCheckup(slot.getId(), "Patient A");
            });
            Future<String> patientB = pool.submit(() -> {
                startGate.await();
                return service.bookCheckup(slot.getId(), "Patient B");
            });

            startGate.countDown();

            int successCount = 0;
            int fullCount = 0;

            for (Future<String> f : List.of(patientA, patientB)) {
                try {
                    String msg = f.get();
                    assertTrue(msg.startsWith("Booked successfully for "));
                    successCount++;
                } catch (ExecutionException e) {
                    assertTrue(e.getCause() instanceof SlotFullException);
                    fullCount++; // Gets clean "slot is full!" error
                }
            }

            assertEquals(1, successCount); // Exactly 1 patient booked
            assertEquals(1, fullCount);    // Second patient waited, saw 0 slots, and got rejected cleanly
            assertEquals(0, repo.findById(slot.getId()).orElseThrow().getAvailableSlots());
        } finally {
            pool.shutdown();
            pool.awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    @Test
    void restApiConcurrentBookings_Returns200And409Conflict() throws Exception {
        CheckupSlot slot = repo.save(new CheckupSlot("Dr. Lan - 10:00 AM", 1));
        String url = "/api/slots/" + slot.getId() + "/book";

        ExecutorService pool = Executors.newFixedThreadPool(2);
        CountDownLatch startGate = new CountDownLatch(1);

        try {
            Future<ResponseEntity<String>> reqA = pool.submit(() -> {
                startGate.await();
                return restTemplate.postForEntity(url, new BookingRequest("Patient A"), String.class);
            });
            Future<ResponseEntity<String>> reqB = pool.submit(() -> {
                startGate.await();
                return restTemplate.postForEntity(url, new BookingRequest("Patient B"), String.class);
            });

            startGate.countDown();

            List<HttpStatus> statuses = List.of(
                    HttpStatus.valueOf(reqA.get().getStatusCode().value()),
                    HttpStatus.valueOf(reqB.get().getStatusCode().value())
            );

            assertTrue(statuses.contains(HttpStatus.OK));
            assertTrue(statuses.contains(HttpStatus.CONFLICT));
            assertEquals(0, repo.findById(slot.getId()).orElseThrow().getAvailableSlots());
        } finally {
            pool.shutdown();
            pool.awaitTermination(5, TimeUnit.SECONDS);
        }
    }
}

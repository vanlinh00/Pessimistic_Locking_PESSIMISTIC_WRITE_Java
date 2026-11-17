package com.hospital.booking.service;

import com.hospital.booking.entity.CheckupSlot;
import com.hospital.booking.exception.SlotFullException;
import com.hospital.booking.exception.SlotNotFoundException;
import com.hospital.booking.repository.CheckupSlotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingService {

    private final CheckupSlotRepository repo;

    public BookingService(CheckupSlotRepository repo) {
        this.repo = repo;
    }

    /**
     * Lock stays active from findByIdForUpdate() until this @Transactional method finishes.
     * Patient B waits on findByIdForUpdate() until Patient A commits.
     */
    @Transactional
    public String bookCheckup(Long slotId, String patientName) {
        CheckupSlot slot = repo.findByIdForUpdate(slotId)
                .orElseThrow(() -> new SlotNotFoundException(slotId));

        if (slot.getAvailableSlots() <= 0) {
            throw new SlotFullException("Sorry " + patientName + ", this checkup slot is full!");
        }

        slot.setAvailableSlots(slot.getAvailableSlots() - 1);
        return "Booked successfully for " + patientName;
    }
}

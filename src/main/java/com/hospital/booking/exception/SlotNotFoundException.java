package com.hospital.booking.exception;

public class SlotNotFoundException extends RuntimeException {

    public SlotNotFoundException(Long slotId) {
        super("Checkup slot not found with id: " + slotId);
    }
}

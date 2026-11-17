package com.hospital.booking.exception;

public class SlotFullException extends RuntimeException {

    public SlotFullException(String message) {
        super(message);
    }
}

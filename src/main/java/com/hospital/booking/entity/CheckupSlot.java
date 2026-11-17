package com.hospital.booking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Normal JPA Entity (no @Version needed).
 * Concurrency is handled at the database row level using PESSIMISTIC_WRITE (SELECT ... FOR UPDATE).
 */
@Entity
@Table(name = "checkup_slot")
public class CheckupSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String doctorName;

    @Column(nullable = false)
    private int availableSlots;

    public CheckupSlot() {
    }

    public CheckupSlot(String doctorName, int availableSlots) {
        this.doctorName = doctorName;
        this.availableSlots = availableSlots;
    }

    public Long getId() {
        return id;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public int getAvailableSlots() {
        return availableSlots;
    }

    public void setAvailableSlots(int availableSlots) {
        this.availableSlots = availableSlots;
    }
}

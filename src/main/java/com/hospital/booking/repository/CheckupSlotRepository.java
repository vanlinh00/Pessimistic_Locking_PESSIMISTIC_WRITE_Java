package com.hospital.booking.repository;

import com.hospital.booking.entity.CheckupSlot;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CheckupSlotRepository extends JpaRepository<CheckupSlot, Long> {

    /**
     * Forces the database to run: SELECT ... FROM checkup_slot WHERE id = ? FOR UPDATE.
     * Concurrent transactions must wait here until the lock holder commits or rolls back.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM CheckupSlot s WHERE s.id = :id")
    Optional<CheckupSlot> findByIdForUpdate(@Param("id") Long id);
}

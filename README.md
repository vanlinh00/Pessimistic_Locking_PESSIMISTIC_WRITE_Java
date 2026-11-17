# Optimistic Locking vs. Pessimistic Locking

> **Core Difference:**  
> * **Optimistic Locking** lets everyone try at once and **returns errors** when conflicts happen.  
> * **Pessimistic Locking** makes users wait in line and finishes **without errors**.

---

## Quick Code & SQL Cheat Sheet

### 1. Pessimistic Locking (Wait in Line — No Errors)
* **Java / JPA:** `@Lock(LockModeType.PESSIMISTIC_WRITE)` *(or `PESSIMISTIC_READ`)*
* **SQL:** `SELECT ... FOR UPDATE`
* **Behavior:** Locks the row first so others wait their turn. **Result: 0 errors.**

```sql
-- Locks the row until the transaction finishes
SELECT * FROM appointments WHERE id = 1 FOR UPDATE;
```

### 2. Optimistic Locking (Check Version — Returns Error)
* **Java / JPA:** `@Version`
* **SQL:** `UPDATE ... WHERE id = ? AND version = ?`
* **Behavior:** Checks the version on save. If someone else saved first, **it throws an error** (`OptimisticLockException`).

```sql
-- Fails (0 rows updated / throws error) if version changed
UPDATE appointments 
SET status = 'BOOKED', version = version + 1 
WHERE id = 1 AND version = 1;
```

---

## Example 1: Doctor Appointment Booking

Imagine a famous doctor has **only 1 appointment left** at 9:00 AM, and **100 patients** click **"Book Now"** at the exact same second:

* ❌ **Optimistic Locking (Returns Errors):**  
  All 100 patients try to book at the same time. **1 patient succeeds, and 99 patients get an error.** The system wastes energy processing 99 failed requests, and those 99 patients see a frustrating error screen.

* ✅ **Pessimistic Locking (Without Errors):**  
  All 100 patients stand in a neat line. **Patient #1** locks the slot, books the appointment, and finishes. **Patient #2** goes next, sees the slot is full, and gets a clean *"Sorry, this time is full"* message—**no errors and no crashes**.

---

## Example 2: Coffee Shop Bank Payments

Imagine a popular coffee shop receives **100 payments** at the exact same second:

* ❌ **Optimistic Locking (Returns Errors):**  
  All 100 payments try to update the coffee shop's bank balance at once. **1 payment succeeds, and 99 payments fail with an error.** The server has to retry those 99 failed payments again and again, slowing everything down.

* ✅ **Pessimistic Locking (Without Errors):**  
  All 100 payments stand in a neat line. Payment #1 finishes, then #2, then #3... **0 errors, and every payment succeeds smoothly.**

---

## Summary Comparison

| Feature | Pessimistic Locking | Optimistic Locking |
| :--- | :--- | :--- |
| **Annotation / SQL** | `@Lock(PESSIMISTIC_WRITE)` / `FOR UPDATE` | `@Version` / `WHERE version = ?` |
| **How it works** | Locks first; others wait in line | Checks version at the end |
| **Under heavy traffic** | **Smooth — Without errors** | **Throws errors** (`OptimisticLockException`) |
| **Best for** | Bank transfers, ticket/doctor bookings | Editing user profiles, low-conflict forms |
package com.att.tdp.popcorn_palace.controllers;

import com.att.tdp.popcorn_palace.entities.Booking;
import com.att.tdp.popcorn_palace.services.BookingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public Map<String, UUID> bookTicket(@Valid @RequestBody BookingRequest request) {
        Booking booking = bookingService.bookTicket(
                request.getShowtimeId(),
                request.getSeatNumber(),
                request.getUserId());
        return Map.of("bookingId", booking.getBookingId());
    }

    // DTO
    @Data
    public static class BookingRequest {
        @NotNull(message = "Showtime ID is required")
        private Long showtimeId;

        @Min(value = 1, message = "Seat number must be at least 1")
        private int seatNumber;

        @NotNull(message = "User ID is required")
        private UUID userId;
    }
}

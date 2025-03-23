package com.att.tdp.popcorn_palace.controllers;

import com.att.tdp.popcorn_palace.entities.Showtime;
import com.att.tdp.popcorn_palace.services.ShowtimeService;
import com.att.tdp.popcorn_palace.util.InputUtils;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.Data;
import jakarta.validation.Valid;


import java.time.LocalDateTime;

@RestController
@RequestMapping("/showtimes")
public class ShowtimeController {

    private final ShowtimeService showtimeService;

    public ShowtimeController(ShowtimeService showtimeService) {
        this.showtimeService = showtimeService;
    }

    @GetMapping("/{showtimeId}")
    public Showtime getShowtime(@PathVariable Long showtimeId) {
        return showtimeService.getShowtime(showtimeId);
    }

    @PostMapping
    public Showtime addShowtime(@Valid @RequestBody ShowtimeRequest request) {
        // Normalize and validate the theater name.
        String normalizedTheater = InputUtils.normalizeString(request.getTheater());
        if (normalizedTheater == null || normalizedTheater.isEmpty()) {
            throw new IllegalArgumentException("Theater name must not be empty");
        }

        // Set showtime parameters.
        Showtime showtime = new Showtime();
        showtime.setId(null);
        showtime.setPrice(request.getPrice());
        showtime.setStartTime(request.getStartTime());
        showtime.setEndTime(request.getEndTime());

        // Add the showtime to the database.
        return showtimeService.addShowtime(showtime, request.getMovieId(), normalizedTheater);
    }

    @PostMapping("/update/{showtimeId}")
    public ResponseEntity<Void> updateShowtime(@PathVariable Long showtimeId, @Valid @RequestBody ShowtimeRequest request) {
        // Normalize and validate theater name.
        String normalizedTheater = InputUtils.normalizeString(request.getTheater());
        if (normalizedTheater == null || normalizedTheater.isEmpty()) {
            throw new IllegalArgumentException("Theater name must not be empty");
        }

        // Set showtime parameters.
        Showtime showtime = new Showtime();
        showtime.setPrice(request.getPrice());
        showtime.setStartTime(request.getStartTime());
        showtime.setEndTime(request.getEndTime());

        // Update the showtime in the database.
        showtimeService.updateShowtime(showtimeId, showtime, request.getMovieId(), normalizedTheater);

        // Return 200 OK with no body.
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{showtimeId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteShowtime(@PathVariable Long showtimeId) {
        showtimeService.deleteShowtime(showtimeId);
    }

    // DTO
    @Data
    public static class ShowtimeRequest {

        @NotNull(message = "Movie ID is required")
        private Long movieId;

        @NotBlank(message = "Theater name is required")
        @Size(max = 100, message = "Theater name must not exceed 100 characters")
        private String theater;

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
        private Double price;

        @NotNull(message = "Start time is required")
        private LocalDateTime startTime;

        @NotNull(message = "End time is required")
        private LocalDateTime endTime;
    }
}

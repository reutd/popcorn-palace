package com.att.tdp.popcorn_palace.controllers;

import com.att.tdp.popcorn_palace.entities.Theater;
import com.att.tdp.popcorn_palace.services.TheaterService;
import com.att.tdp.popcorn_palace.util.InputUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/theaters")
public class TheaterController {

    private final TheaterService theaterService;

    public TheaterController(TheaterService theaterService) {
        this.theaterService = theaterService;
    }

    @PostMapping
    public Theater addTheater(@Valid @RequestBody TheaterRequest request) {
        // Normalize and validate the theater name.
        String normalizedName = InputUtils.normalizeString(request.getName());
        if (normalizedName == null || normalizedName.isEmpty()) {
            throw new IllegalArgumentException("Theater name must not be empty");
        }

        // Set theater parameters.
        Theater theater = new Theater();
        theater.setCapacity(request.getCapacity());
        theater.setName(normalizedName);

        // Add the theater to the database.
        return theaterService.addTheater(theater);
    }

    @GetMapping("/all")
    public List<Theater> getAllTheaters() {
        return theaterService.getAllTheaters();
    }

    @GetMapping("/{id}")
    public Theater getTheater(@PathVariable Long id) {
        return theaterService.getTheater(id);
    }

    @GetMapping("/name/{name}")
    public Theater getTheaterByName(@PathVariable String name) {
        String normalizedName = InputUtils.normalizeString(name);
        return theaterService.getTheaterByName(normalizedName);
    }

    @PostMapping("/update/{id}")
    public Theater updateTheater(@PathVariable Long id, @Valid @RequestBody TheaterRequest request) {
        // Normalize and validate the theater name.
        String normalizedName = InputUtils.normalizeString(request.getName());
        if (normalizedName == null || normalizedName.isEmpty()) {
            throw new IllegalArgumentException("Theater name must not be empty");
        }

        // Set theater parameters.
        Theater theater = new Theater();
        theater.setCapacity(request.getCapacity());
        theater.setName(normalizedName);

        // Update the theater in the database.
        return theaterService.updateTheater(id, theater);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteTheater(@PathVariable Long id) {
        theaterService.deleteTheater(id);
    }

    // DTO
    @Data
    public static class TheaterRequest {
        @Min(value = 1, message = "Capacity must be at least 1")
        private int capacity;

        @NotBlank(message = "Name is required")
        private String name;
    }
}

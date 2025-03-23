package com.att.tdp.popcorn_palace.controllers;

import com.att.tdp.popcorn_palace.entities.Movie;
import com.att.tdp.popcorn_palace.services.MovieService;
import com.att.tdp.popcorn_palace.util.InputUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/all")
    public List<Movie> getAllMovies() {
        return movieService.getAllMovies();
    }

    @PostMapping
    public Movie addMovie(@Valid @RequestBody MovieRequest request) {
        // Normalize the movie title before adding it to the database.
        String normalizedTitle = InputUtils.normalizeString(request.getTitle());
        if (normalizedTitle == null || normalizedTitle.isEmpty()) {
            throw new IllegalArgumentException("Movie title must not be empty");
        }

        // Set movie parameters.
        Movie movie = new Movie();
        movie.setTitle(normalizedTitle);
        movie.setGenre(request.getGenre());
        movie.setDuration(request.getDuration());
        movie.setRating(request.getRating());
        movie.setReleaseYear(request.getReleaseYear());

        // Save to the database.
        return movieService.addMovie(movie);
    }

    @PostMapping("/update/{movieTitle}")
    public ResponseEntity<Void> updateMovie(@PathVariable String movieTitle, @Valid @RequestBody MovieRequest request) {
        // Normalize the input title and the updated title.
        String normalizedMovieTitle = InputUtils.normalizeString(movieTitle);
        String normalizedNewTitle = InputUtils.normalizeString(request.getTitle());
        if (normalizedNewTitle == null || normalizedNewTitle.isEmpty()) {
            throw new IllegalArgumentException("Movie title must not be empty");
        }

        // Set new movie parameters.
        Movie movie = new Movie();
        movie.setTitle(normalizedNewTitle);
        movie.setGenre(request.getGenre());
        movie.setDuration(request.getDuration());
        movie.setRating(request.getRating());
        movie.setReleaseYear(request.getReleaseYear());

        // Save to the database.
        movieService.updateMovie(normalizedMovieTitle, movie);

        // Return 200 OK with no body.
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{movieTitle}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteMovie(@PathVariable String movieTitle) {
        // Normalize the movie title before attempting to delete.
        String normalizedMovieTitle = InputUtils.normalizeString(movieTitle);
        movieService.deleteMovie(normalizedMovieTitle);
    }

    // DTO
    @Data
    public static class MovieRequest {
        @NotBlank(message = "Title is required")
        private String title;

        @NotBlank(message = "Genre is required")
        private String genre;

        @NotNull(message = "Duration is required")
        @Min(value = 1, message = "Duration must be at least 1 minute")
        private Integer duration;

        @NotNull(message = "Rating is required")
        @DecimalMin(value = "0.0", inclusive = true, message = "Rating must be at least 0")
        @DecimalMax(value = "10.0", inclusive = true, message = "Rating must be at most 10")
        private Double rating;

        @NotNull(message = "Release year is required")
        @Min(value = 1000, message = "Release year must be no earlier than 1000")
        @Max(value = 9999, message = "Release year must be no later than 9999")
        private Integer releaseYear;
    }
}

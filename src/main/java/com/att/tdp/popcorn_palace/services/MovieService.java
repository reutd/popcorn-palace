package com.att.tdp.popcorn_palace.services;

import com.att.tdp.popcorn_palace.entities.Movie;
import com.att.tdp.popcorn_palace.entities.Showtime;
import com.att.tdp.popcorn_palace.exceptions.MovieDeletionException;
import com.att.tdp.popcorn_palace.exceptions.ResourceNotFoundException;
import com.att.tdp.popcorn_palace.exceptions.UniqueConstraintViolationException;
import com.att.tdp.popcorn_palace.repositories.MovieRepository;
import com.att.tdp.popcorn_palace.repositories.ShowtimeRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final ShowtimeRepository showtimeRepository;

    public MovieService(MovieRepository movieRepository,
                        ShowtimeRepository showtimeRepository) {
        this.movieRepository = movieRepository;
        this.showtimeRepository = showtimeRepository;
    }

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public Movie addMovie(Movie movie) {
        // Wrap the save operation in a try/catch block to catch any database constraint violations.
        try {
            return movieRepository.save(movie);
        } catch (DataIntegrityViolationException ex) {
            throw new UniqueConstraintViolationException(
                    "Movie title must be unique. " + ex.getMostSpecificCause().getMessage()
            );
        }
    }

    public Movie updateMovie(String movieTitle, Movie updatedMovie) {
        // Lookup the movie by title. If it's not found, throw a ResourceNotFoundException.
        Movie movie = movieRepository.findByTitle(movieTitle)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found: " + movieTitle));

        // Set movie parameters.
        movie.setTitle(updatedMovie.getTitle());
        movie.setGenre(updatedMovie.getGenre());
        movie.setDuration(updatedMovie.getDuration());
        movie.setRating(updatedMovie.getRating());
        movie.setReleaseYear(updatedMovie.getReleaseYear());

        // Wrap the save operation in a try/catch block to catch any database constraint violations.
        try {
            return movieRepository.save(movie);
        } catch (DataIntegrityViolationException ex) {
            throw new UniqueConstraintViolationException(
                    "Movie title must be unique. " + ex.getMostSpecificCause().getMessage()
            );
        }
    }

    public void deleteMovie(String movieTitle) {
        // Lookup the movie by title. If it's not found, throw a ResourceNotFoundException.
        Movie movie = movieRepository.findByTitle(movieTitle)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found: " + movieTitle));

        // Check if there are any showtimes using this movie. If so, it can't be deleted until they are deleted.
        List<Showtime> showtimes = showtimeRepository.findByMovie(movie);
        if (!showtimes.isEmpty()) {
            String ids = showtimes.stream()
                    .map(s -> s.getId().toString())
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");
            throw new MovieDeletionException("Cannot delete movie because it is used by showtimes with IDs: " + ids);
        }

        // Delete the movie.
        movieRepository.delete(movie);
    }
}

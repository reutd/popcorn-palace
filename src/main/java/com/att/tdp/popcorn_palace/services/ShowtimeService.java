package com.att.tdp.popcorn_palace.services;

import com.att.tdp.popcorn_palace.entities.Movie;
import com.att.tdp.popcorn_palace.entities.Showtime;
import com.att.tdp.popcorn_palace.entities.Theater;
import com.att.tdp.popcorn_palace.exceptions.*;
import com.att.tdp.popcorn_palace.repositories.BookingRepository;
import com.att.tdp.popcorn_palace.repositories.MovieRepository;
import com.att.tdp.popcorn_palace.repositories.ShowtimeRepository;
import com.att.tdp.popcorn_palace.repositories.TheaterRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ShowtimeService {

    private final ShowtimeRepository showtimeRepository;
    private final TheaterRepository theaterRepository;
    private final MovieRepository movieRepository;
    private final BookingRepository bookingRepository;

    public ShowtimeService(ShowtimeRepository showtimeRepository,
                           TheaterRepository theaterRepository,
                           BookingRepository bookingRepository,
                           MovieRepository movieRepository) {
        this.showtimeRepository = showtimeRepository;
        this.theaterRepository = theaterRepository;
        this.bookingRepository = bookingRepository;
        this.movieRepository = movieRepository;
    }


    public Showtime addShowtime(Showtime showtime, Long movieId, String theaterName) {
        // Lookup the movie by ID. If it's not found, throw a ResourceNotFoundException.
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found: " + movieId));

        // Lookup the theater by name. if it's not found, create a new one with DEFAULT_CAPACITY.
        Theater theater = theaterRepository.findByName(theaterName)
                .orElseGet(() -> {
                    Theater newTheater = new Theater(theaterName);
                    return theaterRepository.save(newTheater);
                });

        // Set showtime parameters.
        showtime.setMovie(movie);
        showtime.setTheater(theater);

        // Ensure not inadvertently updating an existing record.
        showtime.setId(null);

        // Validate the duration is positive. If not, throw an IllegalArgumentException.
        if (!showtime.getStartTime().isBefore(showtime.getEndTime())) {
            throw new IllegalArgumentException("Showtime startTime must be before endTime");
        }

        // Check for overlapping showtimes before saving. If an overlap exists, throw a OverlappingShowtimeException.
        List<Showtime> overlapping = showtimeRepository.findOverlappingShowtimes(
                theater, showtime.getEndTime(), showtime.getStartTime());

        if (!overlapping.isEmpty()) {
            throw new OverlappingShowtimeException("This showtime overlaps with an existing one in the same theater.");
        }

        // Wrap the save operation in a try/catch block to catch any database constraint violations.
        try {
            return showtimeRepository.save(showtime);
        } catch (DataIntegrityViolationException ex) {
            throw new UniqueConstraintViolationException("Failed to create showtime for movie: " + movie.getTitle() + ": "
                    + ex.getMostSpecificCause().getMessage());
        }
    }


    public Showtime updateShowtime(Long showtimeId, Showtime updatedShowtime, Long movieId, String theaterName) {
        // Lookup the showtime by ID. If it's not found, throw a ResourceNotFoundException.
        Showtime existing = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new ResourceNotFoundException("Showtime not found: " + showtimeId));

        // Lookup the theater by name. If it's not found, throw a ResourceNotFoundException.
        Theater theater = theaterRepository.findByName(theaterName)
                .orElseGet(() -> theaterRepository.save(new Theater(theaterName)));

        // Lookup the movie by ID. If it's not found, throw a ResourceNotFoundException.
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found: " + movieId));

        // Set showtime parameters.
        existing.setPrice(updatedShowtime.getPrice());
        existing.setStartTime(updatedShowtime.getStartTime());
        existing.setEndTime(updatedShowtime.getEndTime());
        existing.setTheater(theater);
        existing.setMovie(movie);

        // Validate the duration is positive. If not, throw an IllegalArgumentException.
        if (!updatedShowtime.getStartTime().isBefore(updatedShowtime.getEndTime())) {
            throw new IllegalArgumentException("Showtime startTime must be before endTime");
        }

        // Check for overlapping showtimes before saving. If an overlap exists, throw a OverlappingShowtimeException.
        List<Showtime> overlapping = showtimeRepository.findOverlappingShowtimes(
                theater, updatedShowtime.getEndTime(), updatedShowtime.getStartTime());

        // Exclude the current showtime from the overlap check.
        overlapping.removeIf(s -> java.util.Objects.equals(s.getId(), showtimeId));
        if (!overlapping.isEmpty()) {
            throw new OverlappingShowtimeException("Updated showtime overlaps with an existing one in the same theater.");
        }

        // Wrap the save operation in a try/catch block to catch any database constraint violations.
        try {
            return showtimeRepository.save(existing);
        } catch (DataIntegrityViolationException ex) {
            throw new UniqueConstraintViolationException("Data integrity violation while updating showtime: "
                    + ex.getMostSpecificCause().getMessage());
        }
    }


    public Showtime getShowtime(Long showtimeId) {
        // Lookup the showtime by ID. If it's not found, throw a ResourceNotFoundException.
        return showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new ResourceNotFoundException("Showtime not found: " + showtimeId));
    }


    public void deleteShowtime(Long showtimeId) {
        // Lookup the showtime by ID. If it's not found, throw a ResourceNotFoundException.
        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new ResourceNotFoundException("Showtime not found: " + showtimeId));

        // Check if there are any bookings associated with this showtime. if so, throw a MovieDeletionException.
        if (bookingRepository.existsByShowtime(showtime)) {
            throw new MovieDeletionException("Cannot delete showtime " + showtimeId +
                    " because it has bookings associated. Please delete the bookings first.");
        }

        // Delete the showtime.
        showtimeRepository.delete(showtime);
    }

}



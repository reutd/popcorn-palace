package com.att.tdp.popcorn_palace.services;

import com.att.tdp.popcorn_palace.entities.Showtime;
import com.att.tdp.popcorn_palace.entities.Theater;
import com.att.tdp.popcorn_palace.exceptions.ResourceNotFoundException;
import com.att.tdp.popcorn_palace.exceptions.TheaterDeletionException;
import com.att.tdp.popcorn_palace.exceptions.UniqueConstraintViolationException;
import com.att.tdp.popcorn_palace.repositories.ShowtimeRepository;
import com.att.tdp.popcorn_palace.repositories.TheaterRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TheaterService {

    private final TheaterRepository theaterRepository;
    private final ShowtimeRepository showtimeRepository;

    public TheaterService(TheaterRepository theaterRepository,
                          ShowtimeRepository showtimeRepository) {
        this.theaterRepository = theaterRepository;
        this.showtimeRepository = showtimeRepository;
    }

    public Theater addTheater(Theater theater) {
        // Wrap the save operation in a try/catch block to catch any database constraint violations.
        try {
            return theaterRepository.save(theater);
        } catch (DataIntegrityViolationException ex) {
            throw new UniqueConstraintViolationException("Theater name must be unique. "
                    + ex.getMostSpecificCause().getMessage());
        }
    }

    public List<Theater> getAllTheaters() {
        return theaterRepository.findAll();
    }

    public Theater getTheater(Long theaterId) {
        // Lookup the theater by ID. If it's not found, throw a ResourceNotFoundException.
        return theaterRepository.findById(theaterId)
                .orElseThrow(() -> new ResourceNotFoundException("Theater not found: " + theaterId));
    }

    public Theater getTheaterByName(String name) {
        // Lookup the theater by name. If it's not found, throw a ResourceNotFoundException.
        return theaterRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Theater not found: " + name));
    }

    public Theater updateTheater(Long theaterId, Theater updatedTheater) {
        // Lookup the theater by ID. If it's not found, throw a ResourceNotFoundException.
        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new ResourceNotFoundException("Theater not found: " + theaterId));

        // Set theater parameters.
        theater.setName(updatedTheater.getName());
        theater.setCapacity(updatedTheater.getCapacity());

        // Wrap the save operation in a try/catch block to catch any database constraint violations.
        try {
            return theaterRepository.save(theater);
        } catch (DataIntegrityViolationException ex) {
            throw new UniqueConstraintViolationException("Theater name must be unique. "
                    + ex.getMostSpecificCause().getMessage());
        }
    }

    public void deleteTheater(Long theaterId) {
        // Lookup the theater by ID. If it's not found, throw a ResourceNotFoundException.
        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new ResourceNotFoundException("Theater not found: " + theaterId));

        // Check if there are any showtimes using this theater. If so, it can't be deleted until they are deleted.
        List<Showtime> showtimes = showtimeRepository.findByTheater(theater);
        if (!showtimes.isEmpty()) {
            String ids = showtimes.stream()
                    .map(s -> s.getId().toString())
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");
            throw new TheaterDeletionException("Cannot delete theater because it is used by showtimes with IDs: " + ids);
        }

        // Delete the theater.
        theaterRepository.delete(theater);
    }
}

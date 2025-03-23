package com.att.tdp.popcorn_palace.services;

import com.att.tdp.popcorn_palace.entities.Booking;
import com.att.tdp.popcorn_palace.entities.Showtime;
import com.att.tdp.popcorn_palace.entities.Theater;
import com.att.tdp.popcorn_palace.exceptions.InvalidSeatException;
import com.att.tdp.popcorn_palace.exceptions.ResourceNotFoundException;
import com.att.tdp.popcorn_palace.repositories.BookingRepository;
import com.att.tdp.popcorn_palace.repositories.ShowtimeRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class    BookingService {

    private final BookingRepository bookingRepository;
    private final ShowtimeRepository showtimeRepository;

    public BookingService(BookingRepository bookingRepository, ShowtimeRepository showtimeRepository) {
        this.bookingRepository = bookingRepository;
        this.showtimeRepository = showtimeRepository;
    }

    public Booking bookTicket(Long showtimeId, int seatNumber, UUID userId) {
        // Lookup the showtime by id. If it's not found, throw a ResourceNotFoundException.
        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new ResourceNotFoundException("Showtime not found: " + showtimeId));

        // Validate the seat number is within the range of available seats in this theater.
        Theater theater = showtime.getTheater();
        if (seatNumber < 1 || seatNumber > theater.getCapacity()) {
            throw new InvalidSeatException("Seat number " + seatNumber + " is out of range. Theater capacity: " + theater.getCapacity());
        }

        // Check if the seat is already booked for this showtime.
        if (bookingRepository.existsByShowtime_IdAndSeatNumber(showtimeId, seatNumber)) {
            throw new InvalidSeatException("Seat number " + seatNumber + " is already booked for this showtime.");
        }

        // Set booking parameters.
        Booking booking = new Booking();
        booking.setShowtime(showtime);
        booking.setSeatNumber(seatNumber);
        booking.setUserId(userId);

        // Wrap the save operation in a try/catch block to catch any database constraint violations.
        try {
            return bookingRepository.save(booking);
        } catch (DataIntegrityViolationException ex) {
            throw new InvalidSeatException("Failed to book seat number " + seatNumber + ": "
                    + ex.getMostSpecificCause().getMessage());
        }
    }
}

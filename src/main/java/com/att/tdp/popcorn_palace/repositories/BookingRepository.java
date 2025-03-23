package com.att.tdp.popcorn_palace.repositories;

import com.att.tdp.popcorn_palace.entities.Booking;
import com.att.tdp.popcorn_palace.entities.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {
    // Checks if a booking already exists with the given showtime ID and seat number.
    boolean existsByShowtime_IdAndSeatNumber(Long showtimeId, int seatNumber);

    // Check if a booking with this showtime exists.
    boolean existsByShowtime(Showtime showtime);


}

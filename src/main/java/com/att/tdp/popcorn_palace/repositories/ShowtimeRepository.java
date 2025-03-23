package com.att.tdp.popcorn_palace.repositories;

import com.att.tdp.popcorn_palace.entities.Movie;
import com.att.tdp.popcorn_palace.entities.Showtime;
import com.att.tdp.popcorn_palace.entities.Theater;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {

    // Query for enforcing the "no overlapping showtimes" constraint.
    @Query("SELECT s FROM Showtime s WHERE s.theater = :theater AND s.startTime <= :newEndTime AND s.endTime >= :newStartTime")
    List<Showtime> findOverlappingShowtimes(@Param("theater") Theater theater,
                                            @Param("newEndTime") LocalDateTime newEndTime,
                                            @Param("newStartTime") LocalDateTime newStartTime);


    List<Showtime> findByMovie(Movie movie);
    List<Showtime> findByTheater(Theater theater);
}

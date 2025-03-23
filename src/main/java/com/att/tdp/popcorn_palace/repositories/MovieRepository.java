package com.att.tdp.popcorn_palace.repositories;

import com.att.tdp.popcorn_palace.entities.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    // Find a movie by the title
    Optional<Movie> findByTitle(String title);

    // Delete a movie by the title
    void deleteByTitle(String title);
}

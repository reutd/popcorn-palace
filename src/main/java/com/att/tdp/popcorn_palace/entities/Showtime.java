package com.att.tdp.popcorn_palace.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "showtimes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Showtime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Auto generated

    @ManyToOne
    @JoinColumn(name = "theater_id", nullable = false)
    @JsonIgnore // Ignore full theater details in the JSON output (will only show the id)
    private Theater theater;

    private double price;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    // Many showtimes can be associated with one movie
    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    @JsonIgnore // Ignore full movie details in the JSON (will only show the id)
    private Movie movie;

    // Expose only the movie id in the JSON response
    @JsonGetter("movieId")
    public Long getMovieId() {
        return movie != null ? movie.getId() : null;
    }

    // Expose only the theater id in the JSON response
    @JsonGetter("theater")
    public String getTheaterName() {
        return theater != null ? theater.getName() : null;
    }
}

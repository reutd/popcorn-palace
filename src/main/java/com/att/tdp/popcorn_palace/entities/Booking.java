package com.att.tdp.popcorn_palace.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(
        name = "bookings",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_showtime_seat",
                columnNames = {"showtime_id", "seat_number"}
        )
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    @GeneratedValue
    private UUID bookingId; // Auto generated

    private int seatNumber;
    @JsonIgnore // Ignore full user details in the JSON output (will only show the id)
    private UUID userId;

    // Many bookings can be associated with one showtime
    @ManyToOne
    @JoinColumn(name = "showtime_id", nullable = false)
    private Showtime showtime;

}

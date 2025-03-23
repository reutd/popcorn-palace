package com.att.tdp.popcorn_palace.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "movies", uniqueConstraints = @UniqueConstraint(columnNames = "title"))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Auto generated

    @Column(unique = true)
    private String title;

    private String genre;
    private int duration;    // Duration in minutes
    private double rating;   // Score between 0 and 10
    private int releaseYear; // 4-digit number from 1000 to 9999
}


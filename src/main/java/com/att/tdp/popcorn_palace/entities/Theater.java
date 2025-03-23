package com.att.tdp.popcorn_palace.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "theaters", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Theater {

    public static final int DEFAULT_CAPACITY = 100; // Default value to use if not set in creation time

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Auto generated

    // Total number of seats in the theater.
    private int capacity;

    @Column(unique = true)
    private String name;

    // Convenience constructor using the default capacity.
    public Theater(String name) {
        this.capacity = DEFAULT_CAPACITY;
        this.name = name;
    }
}

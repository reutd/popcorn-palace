package com.att.tdp.popcorn_palace;

import com.att.tdp.popcorn_palace.entities.Movie;
import com.att.tdp.popcorn_palace.entities.Showtime;
import com.att.tdp.popcorn_palace.entities.Theater;
import com.att.tdp.popcorn_palace.repositories.MovieRepository;
import com.att.tdp.popcorn_palace.repositories.ShowtimeRepository;
import com.att.tdp.popcorn_palace.repositories.TheaterRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.att.tdp.popcorn_palace.util.InputUtils.normalizeString;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional // Each test runs in its own transaction which will be rolled back.
public class PopcornPalaceApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private MovieRepository movieRepository;

	@Autowired
	private TheaterRepository theaterRepository;

	@Autowired
	private ShowtimeRepository showtimeRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@BeforeEach
	void setup() {
		// Clear all repositories before each test.
		showtimeRepository.deleteAll();
		movieRepository.deleteAll();
		theaterRepository.deleteAll();
	}


	// ---------- Movie Tests ----------

	@Test
	void testAddAndGetMovies() throws Exception {
		Movie movie = new Movie(null, "Test Movie", "Action", 120, 8.7, 2025);
		String movieJson = objectMapper.writeValueAsString(movie);

		// Add the movie.
		mockMvc.perform(post("/movies")
						.contentType(MediaType.APPLICATION_JSON)
						.content(movieJson))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.title", is(normalizeString("Test Movie"))));

		// Retrieve all movies.
		mockMvc.perform(get("/movies/all")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].title", is(normalizeString("Test Movie"))));
	}

	@Test
	void testMovieUniqueConstraint() throws Exception {
		Movie movie = new Movie(null, "Unique Movie", "Action", 120, 8.7, 2025);
		String movieJson = objectMapper.writeValueAsString(movie);

		// First insertion should succeed.
		mockMvc.perform(post("/movies")
						.contentType(MediaType.APPLICATION_JSON)
						.content(movieJson))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.title", is(normalizeString("Unique Movie"))));

		// Second insertion should violate the unique constraint.
		mockMvc.perform(post("/movies")
						.contentType(MediaType.APPLICATION_JSON)
						.content(movieJson))
				.andExpect(status().is5xxServerError())
				.andExpect(content().string(containsString("unique")));
	}

	@Test
	void testUpdateNonExistentMovie() throws Exception {
		// Prepare a movie update payload.
		Movie updated = new Movie(null, "Some Title", "Adventure", 130, 9.0, 2026);
		String movieJson = objectMapper.writeValueAsString(updated);

		// Attempt to update a non-existent movie.
		mockMvc.perform(post("/movies/update/NoSuchTitle")
						.contentType(MediaType.APPLICATION_JSON)
						.content(movieJson))
				.andExpect(status().isNotFound())
				// Use normalized expected value.
				.andExpect(content().string(containsString("Movie not found: " + normalizeString("NoSuchTitle"))));
	}

	@Test
	void testDeleteNonExistentMovie() throws Exception {
		// Attempt to delete a non-existent movie.
		mockMvc.perform(delete("/movies/NoSuchTitle")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(content().string(containsString("Movie not found: " + normalizeString("NoSuchTitle"))));
	}



	// ---------- Theater Tests ----------

	@Test
	void testTheaterUniqueConstraint() throws Exception {
		Theater theater = new Theater(null, 100, "Unique Theater");
		String theaterJson = objectMapper.writeValueAsString(theater);

		// First insertion should succeed.
		mockMvc.perform(post("/theaters")
						.contentType(MediaType.APPLICATION_JSON)
						.content(theaterJson))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name", is(normalizeString("Unique Theater"))));

		// Second insertion should fail.
		mockMvc.perform(post("/theaters")
						.contentType(MediaType.APPLICATION_JSON)
						.content(theaterJson))
				.andExpect(status().is5xxServerError())
				.andExpect(content().string(containsString("Unique")));
	}

	@Test
	void testUpdateNonExistentTheater() throws Exception {
		Theater updatedTheater = new Theater(null, 150, "Updated Theater Name");
		String theaterJson = objectMapper.writeValueAsString(updatedTheater);

		// Attempt to update a non-existent theater.
		mockMvc.perform(post("/theaters/update/999")
						.contentType(MediaType.APPLICATION_JSON)
						.content(theaterJson))
				.andExpect(status().isNotFound())
				.andExpect(content().string(containsString("Theater not found: 999")));
	}

	@Test
	void testDeleteNonExistentTheater() throws Exception {
		// Attempt to delete a non-existent theater.
		mockMvc.perform(delete("/theaters/999")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(content().string(containsString("Theater not found: 999")));
	}

	// Get theater by name (using normalization)
	@Test
	void testGetTheaterByName() throws Exception {
		Theater theater = new Theater(null, 100, "Unique Theater");
		String theaterJson = objectMapper.writeValueAsString(theater);

		// First insertion.
		mockMvc.perform(post("/theaters")
						.contentType(MediaType.APPLICATION_JSON)
						.content(theaterJson))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name", is(normalizeString("Unique Theater"))));

		// Lookup by name with extra spaces and different casing.
		String lookupName = "   unique theater   ";
		mockMvc.perform(get("/theaters/name/" + lookupName)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name", is(normalizeString("Unique Theater"))));
	}



	// ---------- Showtime Tests ----------

	@Test
	void testGetNonExistentShowtime() throws Exception {
		// Attempt to get a showtime with a non-existent ID.
		mockMvc.perform(get("/showtimes/9999")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(content().string(containsString("Showtime not found: 9999")));
	}

	@Test
	void testAddShowtimeOverlappingConstraint() throws Exception {
		// Create a movie and a theater.
		Movie movie = new Movie(null, "Showtime Movie", "Drama", 100, 7.5, 2025);
		movie = movieRepository.save(movie);
		Theater theater = new Theater(null, 100, "Overlap Theater");
		theater = theaterRepository.save(theater);

		// First showtime: 10:00 to 12:00. Send theater name (will be normalized in the controller).
		String showtimeJson1 = String.format(
				"{ \"movieId\": %d, \"theater\": \"%s\", \"price\": 20.0, \"startTime\": \"%s\", \"endTime\": \"%s\" }",
				movie.getId(),
				theater.getName(),
				LocalDateTime.of(2025, 2, 14, 10, 0).toString(),
				LocalDateTime.of(2025, 2, 14, 12, 0).toString()
		);
		mockMvc.perform(post("/showtimes")
						.contentType(MediaType.APPLICATION_JSON)
						.content(showtimeJson1))
				.andExpect(status().isOk());

		// Second showtime: 11:00 to 13:00 (should overlap).
		String showtimeJson2 = String.format(
				"{ \"movieId\": %d, \"theater\": \"%s\", \"price\": 25.0, \"startTime\": \"%s\", \"endTime\": \"%s\" }",
				movie.getId(),
				theater.getName(),
				LocalDateTime.of(2025, 2, 14, 9, 30).toString(),
				LocalDateTime.of(2025, 2, 14, 11, 0).toString()
		);
		mockMvc.perform(post("/showtimes")
						.contentType(MediaType.APPLICATION_JSON)
						.content(showtimeJson2))
				.andExpect(status().is5xxServerError())
				.andExpect(content().string(containsString("overlap")));
	}

	@Test
	void testUpdateNonExistentShowtime() throws Exception {
		// Prepare an update payload for a showtime.
		String updatePayload = String.format(
				"{ \"price\": 30.0, \"startTime\": \"%s\", \"endTime\": \"%s\", \"theater\": \"%s\", \"movieId\": %d }",
				LocalDateTime.of(2025, 4, 1, 14, 0).toString(),
				LocalDateTime.of(2025, 4, 1, 16, 0).toString(),
				"NonExistent Theater",
				1L
		);

		// Attempt to update a showtime that doesn't exist.
		mockMvc.perform(post("/showtimes/update/9999")
						.contentType(MediaType.APPLICATION_JSON)
						.content(updatePayload))
				.andExpect(status().isNotFound())
				.andExpect(content().string(containsString("Showtime not found: 9999")));
	}

	@Test
	void testDeleteNonExistentShowtime() throws Exception {
		// Attempt to delete a non-existent showtime.
		mockMvc.perform(delete("/showtimes/9999")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(content().string(containsString("Showtime not found: 9999")));
	}

	@Test
	void testUpdateShowtimeOverlappingConstraint() throws Exception {
		// Create a movie and a theater.
		Movie movie = new Movie(null, "Update Showtime Movie", "Drama", 100, 7.5, 2025);
		movie = movieRepository.save(movie);
		Theater theater = new Theater(null, 100, "Update Overlap Theater");
		theater = theaterRepository.save(theater);

		// Create initial showtime: 10:00 to 12:00.
		String showtimeJson1 = String.format(
				"{ \"movieId\": %d, \"theater\": \"%s\", \"price\": 20.0, \"startTime\": \"%s\", \"endTime\": \"%s\" }",
				movie.getId(),
				theater.getName(),
				LocalDateTime.of(2025, 2, 14, 10, 0).toString(),
				LocalDateTime.of(2025, 2, 14, 12, 0).toString()
		);

		String response1 = mockMvc.perform(post("/showtimes")
						.contentType(MediaType.APPLICATION_JSON)
						.content(showtimeJson1))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		// Create a second showtime that does not overlap initially: 13:00 to 15:00.
		String showtimeJson2 = String.format(
				"{ \"movieId\": %d, \"theater\": \"%s\", \"price\": 22.0, \"startTime\": \"%s\", \"endTime\": \"%s\" }",
				movie.getId(),
				theater.getName(),
				LocalDateTime.of(2025, 2, 14, 13, 0).toString(),
				LocalDateTime.of(2025, 2, 14, 15, 0).toString()
		);
		String response2 = mockMvc.perform(post("/showtimes")
						.contentType(MediaType.APPLICATION_JSON)
						.content(showtimeJson2))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		Showtime secondShowtime = objectMapper.readValue(response2, Showtime.class);
		System.out.println("--- secondShowtime: " + secondShowtime);

		// Build an update payload that causes overlap: 11:30 to 13:30.
		String updatePayload = String.format(
				"{ \"price\": 25.0, \"startTime\": \"%s\", \"endTime\": \"%s\", \"theater\": \"%s\", \"movieId\": %d }",
				LocalDateTime.of(2025, 2, 14, 11, 30).toString(),
				LocalDateTime.of(2025, 2, 14, 13, 30).toString(),
				theater.getName(),
				movie.getId()
		);

		// Perform update and expect an overlapping constraint violation.
		mockMvc.perform(post("/showtimes/update/" + secondShowtime.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(updatePayload))
				.andExpect(status().is5xxServerError())
				.andExpect(content().string(containsString("overlap")));
	}



	// ---------- Booking Tests ----------

	@Test
	void testBookingConstraints() throws Exception {
		Movie movie = new Movie(null, "Booking Movie", "Thriller", 110, 8.0, 2025);
		movie = movieRepository.save(movie);

		Theater theater = new Theater(null, 50, "Booking Theater");
		theater = theaterRepository.save(theater);

		String showtimeJson = String.format(
				"{ \"movieId\": %d, \"theater\": \"%s\", \"price\": 30.0, \"startTime\": \"%s\", \"endTime\": \"%s\" }",
				movie.getId(),
				theater.getName(),
				LocalDateTime.of(2025, 3, 1, 15, 0).toString(),
				LocalDateTime.of(2025, 3, 1, 17, 0).toString()
		);

		String showtimeResponse = mockMvc.perform(post("/showtimes")
						.contentType(MediaType.APPLICATION_JSON)
						.content(showtimeJson))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		Showtime createdShowtime = objectMapper.readValue(showtimeResponse, Showtime.class);

		// Book a valid seat (seat number 10).
		String validBookingJson = String.format(
				"{ \"showtimeId\": %d, \"seatNumber\": %d, \"userId\": \"%s\" }",
				createdShowtime.getId(), 10, "123e4567-e89b-12d3-a456-426614174000"
		);
		mockMvc.perform(post("/bookings")
						.contentType(MediaType.APPLICATION_JSON)
						.content(validBookingJson))
				.andExpect(status().isOk());

		// Attempt to book the same seat again.
		mockMvc.perform(post("/bookings")
						.contentType(MediaType.APPLICATION_JSON)
						.content(validBookingJson))
				.andExpect(status().is5xxServerError())
				.andExpect(content().string(containsString("already booked")));

		// Attempt to book a seat out-of-range (seat number 60 when capacity is 50).
		String outOfRangeBookingJson = String.format(
				"{ \"showtimeId\": %d, \"seatNumber\": %d, \"userId\": \"%s\" }",
				createdShowtime.getId(), 60, "123e4567-e89b-12d3-a456-426614174001"
		);
		mockMvc.perform(post("/bookings")
						.contentType(MediaType.APPLICATION_JSON)
						.content(outOfRangeBookingJson))
				.andExpect(status().is5xxServerError())
				.andExpect(content().string(containsString("out of range")));
	}

	@Test
	void testBookTicketNonExistentShowtime() throws Exception {
		// Attempt to book a ticket for a showtime ID that does not exist.
		String bookingJson = String.format(
				"{ \"showtimeId\": %d, \"seatNumber\": %d, \"userId\": \"%s\" }",
				9999, 10, "123e4567-e89b-12d3-a456-426614174000"
		);
		mockMvc.perform(post("/bookings")
						.contentType(MediaType.APPLICATION_JSON)
						.content(bookingJson))
				.andExpect(status().isNotFound())
				.andExpect(content().string(containsString("Showtime not found: 9999")));
	}


	// ---------- Deletion Constraint Tests ----------

	@Test
	void testDeleteMovieAssociatedWithShowtime() throws Exception {
		// Create a movie.
		Movie movie = new Movie(null, "Delete Test Movie", "Action", 120, 8.7, 2025);
		movie = movieRepository.save(movie);

		// Create a theater.
		Theater theater = new Theater(null, 100, "Associated Theater");
		theater = theaterRepository.save(theater);

		// Create a showtime associated with the movie.
		String showtimeJson = String.format(
				"{ \"movieId\": %d, \"theater\": \"%s\", \"price\": 20.0, \"startTime\": \"%s\", \"endTime\": \"%s\" }",
				movie.getId(),
				theater.getName(),
				LocalDateTime.of(2025, 3, 25, 10, 0).toString(),
				LocalDateTime.of(2025, 3, 25, 12, 0).toString()
		);
		mockMvc.perform(post("/showtimes")
						.contentType(MediaType.APPLICATION_JSON)
						.content(showtimeJson))
				.andExpect(status().isOk());

		// Attempt to delete the movie. Expect an error indicating the movie is in use.
		mockMvc.perform(delete("/movies/" + movie.getTitle())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is5xxServerError())
				.andExpect(content().string(containsString("Cannot delete movie because it is used by showtimes")));
	}

	@Test
	void testDeleteTheaterAssociatedWithShowtime() throws Exception {
		// Create a movie.
		Movie movie = new Movie(null, "Movie For Theater Deletion", "Comedy", 90, 7.0, 2025);
		movie = movieRepository.save(movie);

		// Create a theater.
		Theater theater = new Theater(null, 100, "Delete Test Theater");
		theater = theaterRepository.save(theater);

		// Create a showtime associated with the theater.
		String showtimeJson = String.format(
				"{ \"movieId\": %d, \"theater\": \"%s\", \"price\": 15.0, \"startTime\": \"%s\", \"endTime\": \"%s\" }",
				movie.getId(),
				theater.getName(),
				LocalDateTime.of(2025, 3, 26, 14, 0).toString(),
				LocalDateTime.of(2025, 3, 26, 16, 0).toString()
		);
		mockMvc.perform(post("/showtimes")
						.contentType(MediaType.APPLICATION_JSON)
						.content(showtimeJson))
				.andExpect(status().isOk());

		// Attempt to delete the theater. Expect an error indicating the theater is in use.
		mockMvc.perform(delete("/theaters/" + theater.getId())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is5xxServerError())
				.andExpect(content().string(containsString("Cannot delete theater because it is used by showtimes with IDs")));
	}

	@Test
	void testDeleteShowtimeAssociatedWithBooking() throws Exception {
		// Create a movie.
		Movie movie = new Movie(null, "Showtime Booking Test Movie", "Drama", 120, 8.5, 2025);
		movie = movieRepository.save(movie);

		// Create a theater.
		Theater theater = new Theater(null, 100, "Booking Test Theater");
		theater = theaterRepository.save(theater);

		// Create a showtime.
		String showtimeJson = String.format(
				"{ \"movieId\": %d, \"theater\": \"%s\", \"price\": 18.0, \"startTime\": \"%s\", \"endTime\": \"%s\" }",
				movie.getId(),
				theater.getName(),
				LocalDateTime.of(2025, 3, 27, 18, 0).toString(),
				LocalDateTime.of(2025, 3, 27, 20, 0).toString()
		);
		String showtimeResponse = mockMvc.perform(post("/showtimes")
						.contentType(MediaType.APPLICATION_JSON)
						.content(showtimeJson))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
		Showtime showtime = objectMapper.readValue(showtimeResponse, Showtime.class);

		// Create a booking for that showtime.
		String bookingJson = String.format(
				"{ \"showtimeId\": %d, \"seatNumber\": %d, \"userId\": \"%s\" }",
				showtime.getId(),
				5,
				"123e4567-e89b-12d3-a456-426614174000"
		);
		mockMvc.perform(post("/bookings")
						.contentType(MediaType.APPLICATION_JSON)
						.content(bookingJson))
				.andExpect(status().isOk());

		// Attempt to delete the showtime. Expect an error indicating the showtime is in use.
		mockMvc.perform(delete("/showtimes/" + showtime.getId())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is5xxServerError())
				.andExpect(content().string(containsString("it has bookings associated. Please delete the bookings")));
	}

}


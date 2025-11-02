package com.egger.cinema;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

public class GsonTests {

    @Test
    public void deserializeMovies() throws Exception {
        try (InputStream moviesJson = GsonTests.class.getResourceAsStream("/movies.json")) {

            List<Movie> movies = Mockdata.loadMovies(moviesJson);

            assertThat(movies).isNotNull();
            assertThat(movies).isNotEmpty();
            assertThat(movies).extracting(Movie::title, Movie::durationMinutes, Movie::basePrice, Movie::rating, Movie::genre).containsExactly(tuple("Inception", 148, 12.99, 9.0, "Science Fiction"), tuple("The Godfather", 175, 14.99, 9.2, "Crime"), tuple("Pulp Fiction", 154, 13.99, 8.9, "Crime"), tuple("The Dark Knight", 152, 15.99, 9.0, "Action"), tuple("Forrest Gump", 142, 11.99, 8.8, "Drama"), tuple("The Matrix", 136, 13.49, 8.7, "Science Fiction"), tuple("Interstellar", 169, 16.99, 8.6, "Science Fiction"), tuple("Gladiator", 155, 12.49, 8.5, "Action"), tuple("The Shawshank Redemption", 142, 10.99, 9.3, "Drama"), tuple("Fight Club", 139, 11.49, 8.8, "Drama"), tuple("The Lord of the Rings: The Fellowship of the Ring", 178, 14.49, 8.8, "Fantasy"), tuple("Avatar", 162, 13.99, 7.8, "Science Fiction"), tuple("Titanic", 195, 12.99, 7.8, "Romance"));

            for (Movie movie : movies) {
                assertThat(movie.id()).isNotNull();
                assertThatCode(() -> UUID.fromString(movie.id().toString())).doesNotThrowAnyException();
            }
        }
    }

    @Test
    public void deserializeMoviesInvalidDataInvalidUUID() throws Exception {
        String json = """
                [ { "id": "12345", "title": "Invalid ID", "durationMinutes": 90, "basePrice": 10.0, "rating": 8.0, "genre": "Action" } ]
                """;

        try (InputStream is = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))) {
            assertThatThrownBy(() -> Mockdata.loadMovies(is)).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Invalid UUID");
        }
    }

    @Test
    public void deserializeMovies_invalidData_missingFields() throws Exception {
        String json = """
                [{
                  "id": "11111111-2222-3333-4444-555555555555",
                  "movieName": "Broken Movie",
                  "basePrice": 5.0,
                  "rating": 7.0,
                  "genre": "Drama"
                }]
                """;


        try (InputStream is = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))) {
            assertThatThrownBy(() -> Mockdata.loadMovies(is)).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("durationMinutes");
        }
    }

    @Test
    public void deserializeMovies_invalidData_outOfRange() throws Exception {
        String json = """
                [ { 
                    "id": "11111111-2222-3333-4444-555555555555",
                    "title": "Weird Movie",
                    "durationMinutes": 100,
                    "basePrice": 5.0,
                    "rating": 15.0,
                    "genre": "drama"
                  } ]
                """;


        try (InputStream is = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))) {
            assertThatThrownBy(() -> Mockdata.loadMovies(is)).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("rating");
        }
    }

    @Test
    public void deserializeMovies_invalidData_negativeValues() throws Exception {
        String json = """
                [ {
                  "id": "11111111-2222-3333-4444-555555555555",
                  "title": "Negative Price Movie",
                  "durationMinutes": 100,
                  "basePrice": -5.0,
                  "rating": 5.0,
                  "genre": "comedy"
                } ]
                """;

        try (InputStream is = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))) {
            assertThatThrownBy(() -> Mockdata.loadMovies(is))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("basePrice");
        }
    }


    @Test
    public void deserializeMovies_invalidData_blankTitle() throws Exception {
        String json = """
                [ { "title": "   ", "durationMinutes": 100, "basePrice": 5.0, "rating": 5.0, "genre": "comedy" } ]
                """;

        try (InputStream is = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))) {
            assertThatThrownBy(() -> Mockdata.loadMovies(is)).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("title/movieName");
        }
    }

    @Test
    public void deserializeMovies_emptyList() throws Exception {
        String json = "[]";

        try (InputStream is = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))) {
            List<Movie> movies = Mockdata.loadMovies(is);
            assertThat(movies).isNotNull();
            assertThat(movies).isEmpty();
        }
    }

    @Test
    public void deserializeMovies_nullInputStream() {
        assertThatThrownBy(() -> Mockdata.loadMovies(null)).isInstanceOf(IOException.class).hasMessageContaining("InputStream is null");
    }

    @Test
    public void deserializeMovies_missingResource() {
        assertThatThrownBy(() -> Mockdata.loadMovies(null)).isInstanceOf(IOException.class).hasMessageContaining("InputStream is null");
    }
}

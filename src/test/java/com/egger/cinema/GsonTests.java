package com.egger.cinema;

import org.assertj.core.groups.Tuple;
import org.junit.Test;
import java.io.InputStream;
import java.util.List;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

public class GsonTests {

    @Test
    public void deserializeMovies() throws Exception {
        try (InputStream moviesJson = GsonTests.class.getResourceAsStream("/movies.json")) {

            List<Movie> movies = Mockdata.loadMovies(moviesJson);

            assertThat(movies).isNotNull();
            assertThat(movies).isNotEmpty();
            assertThat(movies)
                    .extracting(
                            Movie::title,
                            Movie::durationMinutes,
                            Movie::basePrice,
                            Movie::rating,
                            Movie::genre
                    )
                    .containsExactly(
                            tuple("Inception", 148, 12.99, 9.0, "Science Fiction"),
                            tuple("The Godfather", 175, 14.99, 9.2, "Crime"),
                            tuple("Pulp Fiction", 154, 13.99, 8.9, "Crime"),
                            tuple("The Dark Knight", 152, 15.99, 9.0, "Action"),
                            tuple("Forrest Gump", 142, 11.99, 8.8, "Drama"),
                            tuple("The Matrix", 136, 13.49, 8.7, "Science Fiction")
                    );

            for (Movie movie : movies) {
                assertThat(movie.id())
                        .isLowerCase()
                        .doesNotContain(" ")
                        .isNotBlank();
            }
        }
    }

    @Test
    public void deserializeMovies_invalidData_missingFields() throws Exception {
        String json = """
                [ { "movieName": "Broken Movie", "basePrice": 5.0 } ]
                """;

        try (InputStream is = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))) {
            assertThatThrownBy(() -> Mockdata.loadMovies(is))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("durationMinutes");
        }
    }

    @Test
    public void deserializeMovies_invalidData_outOfRange() throws Exception {
        String json = """
                [ { "title": "Weird Movie", "durationMinutes": 100, "basePrice": 5.0, "rating": 15.0, "genre": "drama" } ]
                """;

        try (InputStream is = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))) {
            assertThatThrownBy(() -> Mockdata.loadMovies(is))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("rating");
        }
    }

    @Test
    public void deserializeMovies_invalidData_negativeValues() throws Exception {
        String json = """
                [ { "title": "Negative Price Movie", "durationMinutes": 100, "basePrice": -5.0, "rating": 5.0, "genre": "comedy" } ]
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
            assertThatThrownBy(() -> Mockdata.loadMovies(is))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("title/movieName");
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

}

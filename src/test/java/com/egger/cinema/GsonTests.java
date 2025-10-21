package com.egger.cinema;

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
                            tuple("Godzilla", 123, 9.99, 7.9, "action"),
                            tuple("Dune", 155, 5.99, 7.7, "sci-fi"),
                            tuple("Interstellar", 169, 7.99, 9.2, "sci-fi"),
                            tuple("The Ring", 105, 4.99, 7.3, "horror")
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

}

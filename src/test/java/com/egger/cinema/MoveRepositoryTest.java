package com.egger.cinema;

import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.*;

public class MoveRepositoryTest {

    @Test
    public void findById_returnsMovie_whenPresent() throws Exception {
        MovieRepository repo = new MovieRepository();
        UUID inceptionId = UUID.fromString("123E4567-E89B-12D3-A456-426614174000");

        Optional<Movie> result = repo.findById(inceptionId);

        assertThat(result)
                .isPresent()
                .hasValueSatisfying(movie ->
                        assertThat(movie.title()).isEqualTo("Inception"));
    }

    @Test
    public void findById_returnsEmpty_whenUnknownId() throws Exception {
        MovieRepository repo = new MovieRepository();
        UUID fakeId = UUID.fromString("00000000-0000-0000-0000-000000000000");

        Optional<Movie> result = repo.findById(fakeId);

        assertThat(result).isEmpty();
    }

    @Test
    public void findById_throwsOnNullId() throws Exception {
        MovieRepository repo = new MovieRepository();

        assertThatThrownBy(() -> repo.findById(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("id");
    }

    @Test
    public void assertNonDuplicateIds_throwsOnDuplicate() throws IOException {
        UUID duplicateId = UUID.randomUUID();

        Movie movie1 = new Movie(duplicateId, "Title1", 100, 5, 9, "action");
        Movie movie2 = new Movie(duplicateId, "Title2", 100, 5, 9, "action");

        List<Movie> movies = List.of(movie1, movie2);
        MovieRepository repo = new MovieRepository();

        assertThatThrownBy(() -> repo.assertNonDuplicateIds(movies))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Duplicate movie id:");

    }

    @Test
    public void assertNonDuplicateIds_doesNotThrow_whenAllIdsUnique() throws Exception {

        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        Movie m1 = new Movie(id1, "A", 90, 5.0, 7.0, "Drama");
        Movie m2 = new Movie(id2, "B", 95, 6.0, 8.0, "Action");
        List<Movie> movies = List.of(m1, m2);

        MovieRepository repo = new MovieRepository();

        assertThatCode(() -> repo.assertNonDuplicateIds(movies))
                .doesNotThrowAnyException();
    }
}

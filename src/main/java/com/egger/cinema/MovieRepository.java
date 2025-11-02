package com.egger.cinema;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class MovieRepository {
    private final List<Movie> movies;

    public MovieRepository() throws IOException {
        this.movies = load();
    }

    public static List<Movie> load() throws IOException {
        try (InputStream is = MovieRepository.class.getResourceAsStream("/movies.json")) {
            return Mockdata.loadMovies(is);
        }
    }

    public Optional<Movie> findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("id must be null");
        }

        for (Movie movie : movies) {
            if (movie.id().equals(id)) {
                return Optional.of(movie);
            }
        }
        return Optional.empty();
    }

    void assertNonDuplicateIds(List<Movie> movies) {
        HashSet<UUID> uuids = new HashSet<>();

        for (Movie movie : movies) {
            if (!uuids.add(movie.id())) {
                throw new IllegalStateException("Duplicate movie id: " + movie.id());
            }
        }
    }
}

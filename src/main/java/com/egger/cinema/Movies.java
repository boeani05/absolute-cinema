package com.egger.cinema;

import java.io.InputStream;
import java.io.IOException;
import java.util.*;

public class Movies {

    private final List<Movie> all;
    private final Map<String, Movie> byId;

    private Movies(List<Movie> movies) {
        this.all = List.copyOf(movies);
        Map<String, Movie> tmp = new LinkedHashMap<>();
        for (Movie m : movies) {
            tmp.put(m.title().toLowerCase(Locale.ROOT), m);
        }
        this.byId = Map.copyOf(tmp);
    }

    public static Movies fromJsonResource(String resourcePath) {
        try (InputStream is = Movies.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }
            List<Movie> loaded = Mockdata.loadMovies(is);
            return new Movies(loaded);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load movies from " + resourcePath, e);
        }
    }

    public List<Movie> all() {
        return all;
    }

    @SuppressWarnings("unused")
    public Movie get(String title) {
        if (title == null) return null;
        return byId.get(title.toLowerCase(Locale.ROOT));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Movies:\n");
        for (Movie m : all) {
            sb.append(" - ").append(m.title())
                    .append(" (").append(m.genre()).append(")\n");
        }
        return sb.toString();
    }

    public List<Movie> getAll() {
        return all;
    }
}

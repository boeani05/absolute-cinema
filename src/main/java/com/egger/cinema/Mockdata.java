package com.egger.cinema;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

public class Mockdata {

    private static final class MovieJson {
        String id;
        String title;
        String movieName;
        Integer durationMinutes;
        Double basePrice;
        Double rating;
        String genre;
    }

    private static final Gson GSON = new GsonBuilder()
            .serializeNulls()
            .create();

    public static List<Movie> loadMovies(InputStream is) throws IOException {
        if (is == null) {
            throw new IOException("movies.json InputStream is null (resource not found)");
        }

        try (InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            Type listType = new TypeToken<List<MovieJson>>() {}.getType();
            List<MovieJson> raw = GSON.fromJson(reader, listType);
            if (raw == null || raw.isEmpty()) return List.of();

            List<Movie> result = new ArrayList<>(raw.size());
            for (MovieJson r : raw) {
                String title = firstNonBlank(r.title, r.movieName);
                if (isBlank(title)) {
                    throw new IllegalArgumentException("Movie entry missing title/movieName");
                }
                String id = isBlank(r.id) ? slugify(title) : r.id;
                int duration = requirePositive(r.durationMinutes, "durationMinutes");
                double basePrice = requireNonNegative(r.basePrice, "basePrice");
                double rating = requireInRange(r.rating, 0.0, 10.0, "rating");
                String genre = requireNonBlank(r.genre, "genre");

                result.add(new Movie(
                        id,
                        title,
                        duration,
                        basePrice,
                        rating,
                        genre
                ));
            }
            return List.copyOf(result);
        }
    }

    private static String firstNonBlank(String a, String b) {
        return !isBlank(a) ? a : (!isBlank(b) ? b : null);
    }
    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
    private static String requireNonBlank(String s, String field) {
        if (isBlank(s)) throw new IllegalArgumentException("Missing or blank field: " + field);
        return s;
    }
    private static int requirePositive(Integer n, String field) {
        if (n == null || n <= 0) throw new IllegalArgumentException(field + " must be > 0");
        return n;
    }
    private static double requireNonNegative(Double n, String field) {
        if (n == null || n < 0) throw new IllegalArgumentException(field + " must be >= 0");
        return n;
    }
    private static double requireInRange(Double n, double min, double max, String field) {
        if (n == null || n < min || n > max)
            throw new IllegalArgumentException(field + " must be between " + min + " and " + max);
        return n;
    }


    private static final Pattern NON_ALNUM = Pattern.compile("[^a-z0-9]+");
    private static String slugify(String s) {
        String lower = s.toLowerCase(Locale.ROOT);
        String slug = NON_ALNUM.matcher(lower).replaceAll("-");
        slug = slug.replaceAll("(^-+|-+$)", "");
        return slug.isEmpty() ? "movie-" + UUID.randomUUID() : slug;
    }
}

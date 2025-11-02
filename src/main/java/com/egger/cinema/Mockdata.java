package com.egger.cinema;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

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
            Type listType = new TypeToken<List<MovieJson>>() {
            }.getType();
            List<MovieJson> raw = GSON.fromJson(reader, listType);
            if (raw == null || raw.isEmpty()) return List.of();

            List<Movie> result = new ArrayList<>(raw.size());
            for (MovieJson r : raw) {
                String title = firstNonBlank(r.title, r.movieName);
                if (isBlank(title)) {
                    throw new IllegalArgumentException("Movie entry missing title/movieName");
                }

                UUID id = parseUuidOrThrow(r.id);
                int duration = requirePositive(r.durationMinutes);
                double basePrice = requireNonNegative(r.basePrice);
                double rating = requireInRange(r.rating);
                String genre = requireNonBlank(r.genre);

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

    public static void loadData(AbstractCinema cinema) {
        final Map<String, Room> rooms;
        final List<Movie> movies;
        final List<CinemaEvent> events;
        final Map<String, Double> snacks;
        final Map<String, Double> boughtSnacks;

        snacks = new LinkedHashMap<>();
        snacks.put("Popcorn", 5.99);
        snacks.put("Soda", 1.99);
        snacks.put("Nachos", 3.99);

        try (InputStream moviesJson = Mockdata.class.getResourceAsStream("/movies.json")) {

            movies = Mockdata.loadMovies(moviesJson);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        rooms = new TreeMap<>();

        boughtSnacks = new LinkedHashMap<>();

        Movie m1 = randomlySelectMovie(movies);
        Movie m2 = randomlySelectMovie(movies);
        Movie m3 = randomlySelectMovie(movies);
        Movie m4 = randomlySelectMovie(movies);

        CinemaEvent e1 = new CinemaEvent(m1, null, LocalDateTime.now().plusHours(2));
        CinemaEvent e2 = new CinemaEvent(m2, null, LocalDateTime.now().plusHours(3));
        CinemaEvent e3 = new CinemaEvent(m3, null, LocalDateTime.now().plusHours(4));
        CinemaEvent e4 = new CinemaEvent(m4, null, LocalDateTime.now().plusHours(5));

        Room a = new Room(m1, 10, 7, "A", e1);
        Room b = new Room(m2, 5, 5, "B", e2);
        Room c = new Room(m3, 10, 10, "C", e3);
        Room d = new Room(m4, 5, 10, "D", e4);

        e1.setRoom(a);
        e2.setRoom(b);
        e3.setRoom(c);
        e4.setRoom(d);

        rooms.put("A", a);
        rooms.put("B", b);
        rooms.put("C", c);
        rooms.put("D", d);

        events = new ArrayList<>(List.of(e1, e2, e3, e4));

        DiscountCode d1 = new DiscountCode(1, 0.0);
        DiscountCode d2 = new DiscountCode(2, 2.99);
        DiscountCode d3 = new DiscountCode(3, 1.99);
        DiscountCode d4 = new DiscountCode(4, 1.49);

        final Map<Integer, Double> discount = new HashMap<>(Map.of(d1.code(), d1.discount(), d2.code(), d2.discount(), d3.code(), d3.discount(), d4.code(), d4.discount()));

        cinema.setData(rooms, movies, events, discount, snacks);

    }

    public static Movie randomlySelectMovie(List<Movie> movies) {
        Random random = new Random();
        int index = random.nextInt(movies.size());
        return movies.get(index);
    }

    private static String firstNonBlank(String a, String b) {
        return !isBlank(a) ? a : (!isBlank(b) ? b : null);
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static String requireNonBlank(String s) {
        if (isBlank(s)) throw new IllegalArgumentException("genre required");
        return s;
    }

    private static int requirePositive(Integer n) {
        if (n == null || n <= 0) throw new IllegalArgumentException("durationMinutes" + " must be > 0");
        return n;
    }

    private static double requireNonNegative(Double n) {
        if (n == null || n < 0) throw new IllegalArgumentException("basePrice" + " must be >= 0");
        return n;
    }

    private static double requireInRange(Double n) {
        if (n == null || n < 0.0 || n > 10.0)
            throw new IllegalArgumentException("rating" + " must be between " + 0.0 + " and " + 10.0);
        return n;
    }


    private static UUID parseUuidOrThrow(String value) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("Move.id is required");
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID: " + value);
        }
    }
}

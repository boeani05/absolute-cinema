package com.egger.cinema;

public record Movie(
        String id,
        String title,
        int durationMinutes,
        double basePrice,
        double rating,
        String genre
) {
    public Movie {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Movie.id is required!");
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Move.title is required!");
        if (durationMinutes <= 0) throw new IllegalArgumentException("duration must be > 0");
        if (basePrice < 0) throw new IllegalArgumentException("basePrice must be >= 0");
        if (rating < 0 || rating > 10) throw new IllegalArgumentException("rating must be 0..10");
        if (genre == null || genre.isBlank()) throw new IllegalArgumentException("genre required");
    }

    @Override
    public String toString() {
        return title + " (" + durationMinutes + " min, " + genre + ", " + basePrice + ")";
    }

}

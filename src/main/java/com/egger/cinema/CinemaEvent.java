package com.egger.cinema;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CinemaEvent {
    private final Movie movie;
    private Room room;
    private final LocalDateTime startTime;
    private final Map<SeatId, Ticket> bookings;

    public CinemaEvent(Movie movie, Room room, LocalDateTime startTime) {
        this.movie = movie;
        this.room = room;
        this.startTime = startTime;
        this.bookings = new HashMap<>();
    }

    @SuppressWarnings("unused")
    public Movie getMovie() {
        return movie;
    }

    public Room getRoom() {
        return room;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
        return String.format("%s - Room %s at %s", movie.title(), room.getRoomId(), fmt.format(startTime));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CinemaEvent that)) return false;
        assert room != null;
        return Objects.equals(movie.id(), that.movie.id()) &&
                Objects.equals(room.getRoomId(), that.room.getRoomId()) &&
                Objects.equals(startTime, that.startTime);
    }

    @Override
    public int hashCode() {
        assert room != null;
        return Objects.hash(movie.id(), room.getRoomId(), startTime);
    }

    public boolean isValidSeat(int row, int seat) {
        return room.isValidSeat(row, seat);
    }

    public boolean isSeatAvailable(int row, int seat) {
        return !bookings.containsKey(new SeatId(row, seat));
    }

    Ticket book(int row, int seat, double discountValue) throws AlreadyBookedException {
        if (!isValidSeat(row, seat)) {
            throw new IllegalArgumentException("Invalid seat!");
        }

        SeatId id = new SeatId(row, seat);
        Ticket newTicket = new Ticket(row, seat, room.getTicketPrice(discountValue));

        Ticket existing = bookings.putIfAbsent(id, newTicket);
        if (existing != null) {
            throw new AlreadyBookedException("Seat " + seat + " in row " + row + " is already booked!");
        }
        return newTicket;
    }

    public Collection<Ticket> getTickets() {
        return java.util.List.copyOf(bookings.values());
    }

    Map<SeatId, Ticket> getBookings() {
        return bookings;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    @SuppressWarnings("unused")
    public LocalDateTime getEventDateTime() {
        return startTime;
    }

    @SuppressWarnings("unused")
    public Instant getDateTime() {
        return startTime.atZone(java.time.ZoneId.systemDefault()).toInstant();
    }

    public int getSoldTickets() {
        return bookings.size();
    }
}

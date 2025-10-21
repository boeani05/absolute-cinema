package com.egger.cinema;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CinemaEvent {
    private final Movie movie;
    private Room room;
    private final LocalDateTime startTime;
    private final ConcurrentMap<SeatId, Ticket> bookings;

    public CinemaEvent(Movie movie, Room room, LocalDateTime startTime) {
        this.movie = movie;
        this.room = room;
        this.startTime = startTime;
        this.bookings = new ConcurrentHashMap<>();
    }

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
        if (!(o instanceof CinemaEvent)) return false;

        CinemaEvent that = (CinemaEvent) o;
        String thisRoomId = that.room != null ? that.room.getRoomId() : null;
        String thatRoomId = that.room != null ? that.room.getRoomId() : null;
        return Objects.equals(movie.id(), that.movie.id()) &&
                Objects.equals(room.getRoomId(), that.room.getRoomId()) &&
                Objects.equals(startTime, that.startTime);
    }

    @Override
    public int hashCode() {
        String roomId = room != null ? room.getRoomId() : null;
        assert room != null;
        return Objects.hash(movie.id(), room.getRoomId(), startTime);
    }

    public boolean isValidSeat(int row, int seat) {
        return room.isValidSeat(row, seat);
    }

    public boolean isSeatAvailable(int row, int seat) {
        return !bookings.containsKey(new SeatId(row, seat));
    }

    public Ticket book(int row, int seat, int discountCode) throws AlreadyBookedException {
        if (!isValidSeat(row, seat)) {
            throw new IllegalArgumentException("Invalid seat!");
        }

        SeatId id = new SeatId(row, seat);
        Ticket newTicket = new Ticket(row, seat, room.getTicketPrice(discountCode));

        Ticket existing = bookings.putIfAbsent(id, newTicket);
        if (existing != null) {
            throw new AlreadyBookedException("Seat " + seat + " in row " + row + " is already booked!");
        }
        return newTicket;
    }

    public Ticket refund(int row, int seat) throws NotBookedException {
        if (!isValidSeat(row, seat)) {
            throw new IllegalStateException("Invalid seat!");
        }
        SeatId id = new SeatId(row, seat);
        Ticket removed = bookings.remove(id);
        if (removed == null) {
            throw new NotBookedException("Seat " + seat + " in row " + row + " is not booked yet!");
        }
        return removed;
    }

    public Collection<Ticket> getTickets() {
        return Collections.unmodifiableList(new ArrayList<>(bookings.values()));
    }

    public ConcurrentMap<SeatId, Ticket> getBookings() {
        return bookings;
    }

    public void setRoom(Room room) {
        this.room = room;
    }
}

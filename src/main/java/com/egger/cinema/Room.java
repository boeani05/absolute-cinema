package com.egger.cinema;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Room {
    private final int rowsInHall;
    private final int seatsPerRow;
    private final Movie movie;
    private final ConcurrentMap<SeatId, Ticket> bookings;
    private final Map<Integer, Double> discountCode;
    private final String roomId;

    public Room(Movie movie, int rowsInHall, int seatsPerRow, String roomId) {
        this.movie = movie;
        this.rowsInHall = rowsInHall;
        this.seatsPerRow = seatsPerRow;
        this.bookings = new ConcurrentHashMap<>();
        this.discountCode = new TreeMap<>();
        this.roomId = roomId;

        discountCode.put(1, 0.0);
        discountCode.put(2, 3.99);
        discountCode.put(3, 1.99);
        discountCode.put(4, 2.99);
    }

    //Check if input is an existing/valid seat
    public boolean isValidSeat(int selectedRow, int selectedSeatInRow) {

        return selectedRow >= 1 && selectedRow <= rowsInHall && selectedSeatInRow >= 1 && selectedSeatInRow <= seatsPerRow;

    }


    public List<Ticket> getTickets() {
        return Collections.unmodifiableList(new ArrayList<>(bookings.values()));
    }

    public String getMovieName() {
        return movie.title();
    }

    public int getAllSeatsInHall() {
        return rowsInHall * seatsPerRow;
    }

    public int getSoldTickets() {
        return bookings.size();
    }

    public double getSoldTicketsInPercent() {
        return (double) getSoldTickets() / getAllSeatsInHall() * 100;
    }

    public double getIncome() {
        double income = 0;
        for (Ticket ticket : getTickets()) {
            income += ticket.price();
        }
        return income;
    }

    public double getAverageIncome() {
        if (getSoldTickets() <= 0) {
            return 0.0;
        }

        return getIncome() / getSoldTickets();
    }

    public double getPotentialIncome() {
        return getTicketPrice(1) * getAllSeatsInHall();
    }

    public Statistics getStatistics() {
        return new Statistics(getSoldTickets(), getSoldTicketsInPercent(), getIncome(), getAverageIncome(), getPotentialIncome());
    }

    public double getTicketPrice(int code) {
        double price = movie.basePrice() - getDiscountValue(code);

        if (price < 0) price = 0;
        return price;
    }

    public double getDiscountValue(int code) {
        Double d = discountCode.get(code);
        if (d == null) {
            throw new IllegalArgumentException("Invalid discount code: " + code);
        }
        return d;
    }

    public String getRoomId() {
        return roomId;
    }
}

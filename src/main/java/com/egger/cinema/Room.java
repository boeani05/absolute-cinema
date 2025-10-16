package com.egger.cinema;

import java.math.BigDecimal;
import java.util.*;

public class Room {
    private final String movieName;
    private final int rowsInHall;
    private final int seatsPerRow;
    private final Map<SeatId, Ticket> bookings;
    private final BigDecimal basePrice;
    private final Map<Integer, BigDecimal> discountCode;


    //Constructor
    public Room(String movieName, int rowsInHall, int seatsPerRow, BigDecimal basePrice) {
        this.movieName = movieName;

        this.rowsInHall = rowsInHall;
        this.seatsPerRow = seatsPerRow;
        this.bookings = new HashMap<>();
        this.basePrice = basePrice;
        this.discountCode = new TreeMap<>();

        discountCode.put(1, new BigDecimal("0.0"));
        discountCode.put(2, BigDecimal.valueOf(3.99));
        discountCode.put(3, BigDecimal.valueOf(1.99));
        discountCode.put(4, BigDecimal.valueOf(2.99));
    }

    //Check if input is an existing/valid seat
    public boolean isValidSeat(int selectedRow, int selectedSeatInRow) {

        if (selectedRow < 1 || selectedRow > rowsInHall || selectedSeatInRow < 1 || selectedSeatInRow > seatsPerRow) {
            return false;
        }

        return true;

    }

    //check if the seat is already booked
    public boolean isSeatAvailable(int selectedRow, int selectedSeat) {
        return !bookings.containsKey(new SeatId(selectedRow, selectedSeat));
    }

    //TODO: what to do, if card is refunded?
    public Ticket book(int selectedRow, int selectedSeatInRow, int discountCode) throws AlreadyBookedException {
        //throws exception, if input seat is not valid
        if (!isValidSeat(selectedRow, selectedSeatInRow)) {
            throw new IllegalArgumentException("Invalid seat!");
        }
        //throws exception if input seat is alr. booked
        if (!isSeatAvailable(selectedRow, selectedSeatInRow)) {
            throw new AlreadyBookedException("Seat " + selectedSeatInRow + " in row " + selectedRow + " is already booked!");
        }
        //book ticket, if all tests passed
        Ticket ticket = new Ticket(selectedRow, selectedSeatInRow, getTicketPrice(discountCode)); // TODO: fix price later
        //put the seatid(key) and the ticket(value) in bookings map
        bookings.put(new SeatId(selectedRow, selectedSeatInRow), ticket);
        return ticket;
    }

    public List<Ticket> getTickets() {
        return new ArrayList<>(bookings.values());
    }

    public String getMovieName() {
        return movieName;
    }

    public int getRowsInHall() {
        return rowsInHall;
    }

    public int getSeatsPerRow() {
        return seatsPerRow;
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

    public BigDecimal getIncome() {
        BigDecimal income = BigDecimal.ZERO;
        for (Ticket ticket : getTickets()) {
            income = income.add(ticket.price());
        }
        return income;
    }

    public BigDecimal getPotentialIncome() {
        return getTicketPrice(1).multiply(BigDecimal.valueOf(getAllSeatsInHall()));
    }

    public Statistics getStatistics() {
        return new Statistics(getSoldTickets(), getSoldTicketsInPercent(), getIncome(), getPotentialIncome());
    }

    public BigDecimal getTicketPrice(int discountCode) {
        return this.basePrice.subtract(getDiscountValue(discountCode));
    }

    public BigDecimal getDiscountValue(int discountCode) {
        try {
            return this.discountCode.get(discountCode);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}

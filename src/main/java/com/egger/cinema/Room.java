package com.egger.cinema;

import java.util.*;

public class Room {
    private final int rowsInHall;
    private final int seatsPerRow;
    private final Movie movie;
    private CinemaEvent event;
    private final String roomId;

    public Room(Movie movie, int rowsInHall, int seatsPerRow, String roomId, CinemaEvent event) {
        this.movie = movie;
        this.rowsInHall = rowsInHall;
        this.seatsPerRow = seatsPerRow;
        this.event = event;
        if (this.event != null) {
            this.event.setRoom(this);
        }

        this.roomId = roomId;


    }

    //Check if input is an existing/valid seat
    public boolean isValidSeat(int selectedRow, int selectedSeatInRow) {

        return selectedRow >= 1 && selectedRow <= rowsInHall && selectedSeatInRow >= 1 && selectedSeatInRow <= seatsPerRow;

    }

    public String getMovieName() {
        return movie.title();
    }

    public int getAllSeatsInHall() {
        return rowsInHall * seatsPerRow;
    }

    public int getSoldTickets() {
        return event.getBookings().size();
    }

    public double getSoldTicketsInPercent() {
        return (double) getSoldTickets() / getAllSeatsInHall() * 100;
    }

    public double getIncome() {
        double income = 0;
        for (Ticket ticket : event.getTickets()) {
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

    public double getTicketPrice(double discountValue) {
        double price = movie.basePrice() - discountValue;

        if (price < 0) price = 0;
        return price;
    }

    public String getRoomId() {
        return roomId;
    }

    @SuppressWarnings("unused")
    public Movie getMovie() {
        return movie;
    }

    @SuppressWarnings("unused")
    public void setEvent(CinemaEvent event) {
        this.event = event;
    }

    @SuppressWarnings("unused")
    public CinemaEvent getEvent() {
        return event;
    }

    public int getRowsInHall() {
        return rowsInHall;
    }

    public int getSeatsPerRow() {
        return seatsPerRow;
    }

    @Override
    public String toString() {
        return "Room " + roomId + " showing " + movie.title();
    }

    public void printSeatingChart() {
        System.out.print("    ");
        for (int seatNum = 1; seatNum <= seatsPerRow; seatNum++) {
            System.out.printf("%2d ", seatNum);
        }
        System.out.println();

        for (int rowNum = 1; rowNum <= rowsInHall; rowNum++) {
            System.out.printf("%2d |", rowNum);
            for (int seatNum = 1; seatNum <= seatsPerRow; seatNum++) {
                SeatId seatId = new SeatId(rowNum, seatNum);
                if (event.getBookings().containsKey(seatId)) {
                    System.out.print(" X ");
                } else {
                    System.out.print(" O ");
                }
            }
            System.out.println();
        }
    }
}

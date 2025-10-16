package com.egger.cinema;

import java.util.HashMap;
import java.util.Map;

public class Room {
    private final int rowsInHall;
    private final int seatsPerRow;
    private final Map<SeatId, Ticket> bookings;

    //Constructor
    public Room(int rowsInHall, int seatsPerRow) {
        this.rowsInHall = rowsInHall;
        this.seatsPerRow = seatsPerRow;
        this.bookings = new HashMap<>();
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


    public Ticket book(int selectedRow, int selectedSeatInRow) throws AlreadyBookedException {
        //throws exception, if input seat is not valid
        if (!isValidSeat(selectedRow, selectedSeatInRow)) {
            throw new IllegalArgumentException("Invalid seat!");
        }
        //throws exception if input seat is alr booked
        if (!isSeatAvailable(selectedRow, selectedSeatInRow)) {
            throw new AlreadyBookedException("Seat " + selectedSeatInRow + " in row " + selectedRow + " is already booked!");
        }
        //book ticket, if all tests passed
        //TODO: fix price later
        Ticket ticket = new Ticket(selectedRow, selectedSeatInRow, 0);
        //put the seatid(key) and the ticket(value) in bookings map
        bookings.put(new SeatId(selectedRow, selectedSeatInRow), ticket);
        return ticket;
    }
}

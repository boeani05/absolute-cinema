package com.egger.cinema;

import java.util.ArrayList;
import java.util.List;

public class Cinema {
    private final int seatsInRow;
    private final int rowsInCinema;

    @Deprecated
    private final String[][] availableSpacesInCinema;

    private final List<Ticket> soldTickets;


    public Cinema(int seatsInRow, int rowsInCinema) {
        this.seatsInRow = seatsInRow;
        this.rowsInCinema = rowsInCinema;
        this.availableSpacesInCinema = new String[rowsInCinema + 1][seatsInRow + 1];

        soldTickets = new ArrayList<>();
    }


    public boolean isSeatValid(int row, int seat) {
        if (row < 1 || row > rowsInCinema || seat < 1 || seat > seatsInRow) {
            return false;
        }

        return true;
    }

    public int getRowsInCinema() {
        return rowsInCinema;
    }

    public int getSeatsInRow() {
        return seatsInRow;
    }

    public boolean isSeatAvailable(int row, int seat) {
        // not performant
        for (Ticket ticket : soldTickets) {
            if (ticket.rowNr() == row && ticket.seatNr() == seat) {
                return false;
            }
        }

        return true;
    }


    public double getTicketPrice(int rowNumberToBuy, int discount) {
        if (rowsInCinema * seatsInRow <= 60) {
            return 10.0 - getDiscount(discount);
        } else {
            int frontHalfOfCinema = rowsInCinema / 2;
            return rowNumberToBuy <= frontHalfOfCinema ? 10.0  - getDiscount(discount) : 8.0  - getDiscount(discount);
        }
    }

    public double getDiscount(int kindOfDiscount) {
        switch (kindOfDiscount) {
            case 1:
                return 0.0;
            case 2:
                return 4.0;
            case 3:
                return 2.0;
            case 4:
                return 3.0;
        }
        return 0;
    }

    public void bookSeat(int row, int seat, int discount) {
        Ticket ticket = new Ticket(row, seat, getTicketPrice(row, discount));
        soldTickets.add(ticket);
    }

    public Statistics getStatistics() {

        return new Statistics(soldTickets.size(),getPercentage(), getCurrentIncome(),  getPotentialTotalIncome());

    }

    public double getPercentage() {
        if (Double.isNaN((double) soldTickets.size() / getTotalSeatsInCinema() * 100)) {
            return 0.0;
        }
        return (double) soldTickets.size() / getTotalSeatsInCinema() * 100;
    }

    private double getCurrentIncome() {
        double income = 0;

        for (Ticket ticket : soldTickets) {
            income += ticket.price();
        }

        return income;
    }

    public int getTotalSeatsInCinema() {
        return rowsInCinema * seatsInRow;
    }

    public double getPotentialTotalIncome() {
        if (rowsInCinema * seatsInRow <= 60) {
            return rowsInCinema * seatsInRow * 10.0;
        } else {
            int frontHalfOfCinema = rowsInCinema / 2;
            int backHalfOfCinema = rowsInCinema - frontHalfOfCinema;
            return (frontHalfOfCinema * seatsInRow * 10.0) + (backHalfOfCinema * seatsInRow * 8.0);
        }
    }
}
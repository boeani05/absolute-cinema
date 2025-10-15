package com.egger.cinema;

import java.util.ArrayList;
import java.util.List;

public class Cinema {
    private final int seatsInRow;
    private final int rowsInCinema;

    private final List<Ticket> soldTickets;


    public Cinema(int seatsInRow, int rowsInCinema) {
        this.seatsInRow = seatsInRow;
        this.rowsInCinema = rowsInCinema;

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


    public double getTicketPrice(int discount, int movieChosen) {
        switch (movieChosen) {
            case 1:
                return 8.99 - getDiscount(discount);
            case 2:
                return 9.99 - getDiscount(discount);
            case 3:
                return 6.99 - getDiscount(discount);
            case 4:
                return 4.99 - getDiscount(discount);
        }
        return 0;
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

    public Statistics getStatistics(int chosenMovie) {

        return new Statistics(soldTickets.size(),getPercentage(), getCurrentIncome(),  getPotentialTotalIncome(chosenMovie));

    }

    public int getAllSoldTickets(Cinema movie1, Cinema movie2, Cinema movie3, Cinema movie4) {
        return movie1.getStatistics(1).soldTicketAmount() +
               movie2.getStatistics(2).soldTicketAmount() +
               movie3.getStatistics(3).soldTicketAmount() +
               movie4.getStatistics(4).soldTicketAmount();
    }

    public double getAllPercentSold(Cinema movie1, Cinema movie2, Cinema movie3, Cinema movie4) {
        return (movie1.getStatistics(1).percentSold() +
                movie2.getStatistics(2).percentSold() +
                movie3.getStatistics(3).percentSold() +
                movie4.getStatistics(4).percentSold()) / 4;
    }

    public double getAllMaxIncome(Cinema movie1, Cinema movie2, Cinema movie3, Cinema movie4) {
        return movie1.getStatistics(1).maxIncome() +
                movie2.getStatistics(2).maxIncome() +
                movie3.getStatistics(3).maxIncome() +
                movie4.getStatistics(4).maxIncome();
    }

    public double getAllIncome(Cinema movie1, Cinema movie2, Cinema movie3, Cinema movie4) {
        return movie1.getStatistics(1).income() +
                movie2.getStatistics(2).income() +
                movie3.getStatistics(3).income() +
                movie4.getStatistics(4).income();
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

    public double getPotentialTotalIncome(int chosenMovie) {
        return getTotalSeatsInCinema() * getTicketPrice(1, chosenMovie);
    }
}
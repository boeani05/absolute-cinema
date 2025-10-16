package com.egger.cinema;

import java.util.ArrayList;
import java.util.List;

public class Cinema {
    private final int seatsPerRow;
    private final int rows;
    private final Room room;
    private final List<Ticket> soldTickets;


   // private final Map<String, Room> rooms = new LinkedHashMap<>();
// rooms.put("A", new Room(3,6));
    // Room roomA = rooms.get("A")

    //constructor
    public Cinema(int seatsPerRow, int rows) {
        this.seatsPerRow = seatsPerRow;
        this.rows = rows;
        this.room = new Room(rows, seatsPerRow);
        this.soldTickets = new ArrayList<>();

//        for(String roomName : rooms.keySet()){
//            Room room = rooms.get(roomName);
//        }
//
//        for(Map.Entry<String, Room> entry: rooms.entrySet()){
//            entry.getKey();
//            entry.getValue()
//        }
    }
    public int getRowsInCinema() {
        return rows;
    }

    public int getSeatsInRow() {
        return seatsPerRow;
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

    public boolean isSeatValid(int row, int seatInRow) {
        return room.isValidSeat(row, seatInRow);
    }

    public boolean isSeatAvailable(int row, int seatInRow) {
        return room.isSeatAvailable(row, seatInRow);
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

    //FIXME
    public void bookSeat(int row, int seat, int discount, int movieChosen) throws AlreadyBookedException {
        double price = getTicketPrice(discount, movieChosen);
        room.book(row, seat);
        soldTickets.add(new Ticket(row, seat, price));
    }

    public Statistics getStatistics(int chosenMovie) {

        return new Statistics(soldTickets.size(),getPercentage(), getCurrentIncome(),  getPotentialTotalIncome(chosenMovie));

    }

    // List<Cinema> movies
    public int getAllSoldTickets(Cinema movie1, Cinema movie2, Cinema movie3, Cinema movie4) {
       /* int soldTickets = 0;

        for(Cinema movie : movies){
            soldTickets += movie.getStatistics().soldTicketAmount();
        }

        return soldTickets;

        */

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
        return rows * seatsPerRow;
    }

    public double getPotentialTotalIncome(int chosenMovie) {
        return getTotalSeatsInCinema() * getTicketPrice(1, chosenMovie);
    }
}


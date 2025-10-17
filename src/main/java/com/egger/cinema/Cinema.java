package com.egger.cinema;

import java.math.BigDecimal;
import java.util.*;

public class Cinema {
    //private final Room room;
    private final Map<String, Room> rooms;

    //constructor
    public Cinema() {
        this.rooms = new TreeMap<>();


        rooms.put("A", new Room("Godzilla", 10, 7, BigDecimal.valueOf(9.99)));

        rooms.put("B", new Room("Dune", 5, 5, BigDecimal.valueOf(5.99)));

        rooms.put("C", new Room("Interstellar", 10, 10, BigDecimal.valueOf(7.99)));

        rooms.put("D", new Room("The Ring", 5, 10, BigDecimal.valueOf(4.99)));
    }

    public Room getRoom(String id) {
        if (id == null) {
            return null;
        }

        return rooms.get(id.toUpperCase(Locale.ROOT));
    }

    public Map<String, Room> getRooms() {
        return Collections.unmodifiableMap(rooms);
    }

    //method for all sold tickets

    public int getTotalSeatsInCinema() {
        int totalSeats = 0;
        for (Room room : rooms.values()) {
            totalSeats += room.getAllSeatsInHall();
        }
        return totalSeats;
    }

    public double getAllSoldTicketsInPercent() {
        int allSoldTickets = getAllSoldTickets();
        int allSeatsInCinema = getTotalSeatsInCinema();

        if (allSeatsInCinema == 0) {
            return 0.0;
        }

        return (double) (allSoldTickets * 100) / allSeatsInCinema;
    }

    public int getAllSoldTickets() {
        int allSoldTickets = 0;
        for (Room room : rooms.values()) {
            allSoldTickets += room.getSoldTickets();
        }
        return allSoldTickets;
    }

    public BigDecimal getAllIncome() {
        BigDecimal allIncome = BigDecimal.ZERO;
        for (Room room : rooms.values()) {
            allIncome = allIncome.add(room.getIncome());
        }
        return allIncome;
    }

    public BigDecimal getAllTotalPotentialIncome() {
        BigDecimal allPotentialIncome = BigDecimal.ZERO;
        for (Room room : rooms.values()) {
            allPotentialIncome = allPotentialIncome.add(room.getPotentialIncome());
        }

        return allPotentialIncome;
    }

    public Statistics getAllStatistics() {
        return new Statistics(getAllSoldTickets(), getAllSoldTicketsInPercent(), getAllIncome(), getAllTotalPotentialIncome());
    }
}


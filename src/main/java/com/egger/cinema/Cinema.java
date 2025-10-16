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

//        private final Cinema godzillaMovie = new Cinema(10, 7);
//        private final Cinema duneMovie = new Cinema(5, 5);
//        private final Cinema interstellarMovie = new Cinema(10, 10);
//        private final Cinema ringMovie = new Cinema(5, 10);

//        for(String roomName : rooms.keySet()){
//            Room room = rooms.get(roomName);
//        }
//
//        for(Map.Entry<String, Room> entry: rooms.entrySet()){
//            entry.getKey();
//            entry.getValue()
//        }
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

    /*FIXME
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

     */

    //method for all sold tickets

    /* will be used in the future
    public int getTotalSeatsInCinema() {
        return rows * seatsPerRow;
    }

     */
}


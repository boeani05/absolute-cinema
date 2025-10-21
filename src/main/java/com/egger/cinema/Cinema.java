package com.egger.cinema;

import java.time.LocalDateTime;
import java.util.*;


public class Cinema {
    private final Map<String, Room> rooms;
    private final Movies movies;
    private final List<CinemaEvent> events;

    public Cinema() {
        this.movies = Movies.fromJsonResource("/movies.json");
        this.rooms = new TreeMap<>();
        this.events = new ArrayList<>();

        List<Movie> list = movies.all();
        // safety
        Movie m1 = list.size() > 0 ? list.get(0) : new Movie("godzilla-2014","Godzilla",123,9.99,7.9,"action");
        Movie m2 = list.size() > 1 ? list.get(1) : new Movie("dune-2021","Dune",155,5.99,7.7,"sci-fi");
        Movie m3 = list.size() > 2 ? list.get(2) : new Movie("interstellar-2014","Interstellar",169,7.99,9.2,"sci-fi");
        Movie m4 = list.size() > 3 ? list.get(3) : new Movie("ring-2002","The Ring",105,4.99,7.3,"horror");

        rooms.put("A", new Room(m1, 10, 7, "A"));
        rooms.put("B", new Room(m2, 5, 5, "B"));
        rooms.put("C", new Room(m3, 10, 10, "C"));
        rooms.put("D", new Room(m4, 5, 10, "D"));
        createMockEvents();
    }

    public Room getRoom(String id) {
        if (id == null) return null;
        return rooms.get(id.toUpperCase(Locale.ROOT));
    }

    public int getAllSoldTickets() {
        return events.stream()
                .mapToInt(e -> e.getTickets().size())
                .sum();
    }

    public double getAllSoldTicketsInPercent() {
        int sold = getAllSoldTickets();
        int capacityAcrossEvents = events.stream()
                .mapToInt(e -> e.getRoom().getAllSeatsInHall())
                .sum();
        if (capacityAcrossEvents == 0) return 0.0;
        return (sold * 100.0) / capacityAcrossEvents;
    }


    public double getAllIncome() {
        return events.stream()
                .flatMap(e -> e.getTickets().stream())
                .mapToDouble(Ticket::price)
                .sum();
    }

    public double getAllAverageIncome() {
        int sold = getAllSoldTickets();
        if (sold == 0) return 0.0;
        return getAllIncome() / sold;
    }


    public double getAllPotentialIncome() {
        return events.stream()
                .mapToDouble(e -> e.getRoom().getAllSeatsInHall() * e.getRoom().getTicketPrice(1))
                .sum();
    }

    public String getAllStatistics() {
        return new Statistics(getAllSoldTickets(), getAllSoldTicketsInPercent(), getAllIncome(), getAllAverageIncome(), getAllPotentialIncome()).toString();
    }


    public String getPerRoomStatisticsTable() {
        Map<Room, String> idByRoom = new HashMap<>();

        for (Map.Entry<String, Room> e : rooms.entrySet()) {
            idByRoom.put(e.getValue(), e.getKey());
        }

        List<Room> sortedRooms = new ArrayList<>(rooms.values());
        sortedRooms.sort(
                Comparator.comparingDouble((Room r) -> r.getStatistics().percentSold())
                        .reversed()
                        .thenComparing(Room::getMovieName)
        );

        StringBuilder sb = new StringBuilder();
        sb.append("Room | Movie | Sold/Total | % Sold | Income | Potential\n");
        sb.append("--------------------------------------------------------\n");

        for (Room room : sortedRooms) {
            String roomId = idByRoom.get(room);
            Statistics st = room.getStatistics();

            int sold = st.soldTicketAmount();
            int total = room.getAllSeatsInHall();
            double percent = st.percentSold();
            double income = st.income();
            double potential = st.maxIncome();

            sb.append(String.format(
                    "%s | %s | %d/%d | %.2f %%| $%.2f | $%.2f\n",
                    roomId,
                    room.getMovieName(),
                    sold, total,
                    percent,
                    income,
                    potential
            ));
        }
        return sb.toString();
    }

    public void addEvent(CinemaEvent event) {
        if (!events.contains(event)) {
            events.add(event);
        } else {
            System.out.println("This event has already been created!");
        }
    }

    public List<CinemaEvent> getEvents() {
        return Collections.unmodifiableList(events);
    }

    private void createMockEvents() {
        addEvent(new CinemaEvent(movies.all().get(0), getRoom("D"), LocalDateTime.of(2025, 10, 21, 18, 15)));
        addEvent(new CinemaEvent(movies.all().get(1), getRoom("C"), LocalDateTime.of(2025, 10, 21, 18, 45)));
        addEvent(new CinemaEvent(movies.all().get(2), getRoom("B"), LocalDateTime.of(2025, 10, 21, 19, 15)));
        addEvent(new CinemaEvent(movies.all().get(3), getRoom("A"), LocalDateTime.of(2025, 10, 21, 19, 45)));
    }
}

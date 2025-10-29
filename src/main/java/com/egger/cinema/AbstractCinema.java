package com.egger.cinema;

import java.time.LocalDateTime;
import java.util.*;

public abstract class AbstractCinema implements ICinema {

    protected final Map<Integer, Double> discount;
    protected final Map<String, Room> rooms;
    protected final List<Movie> movies;
    protected final List<CinemaEvent> events;
    protected final Map<String, Double> snacks;
    protected final Map<String, Double> boughtSnacks;

    protected AbstractCinema() {
        this.snacks = new LinkedHashMap<>();
        this.movies = new ArrayList<>();
        this.rooms = new TreeMap<>();
        this.events = new ArrayList<>();
        this.discount = new HashMap<>();
        this.boughtSnacks = new LinkedHashMap<>();

        loadData();
    }

    protected abstract void loadData();

    /**
     * Sets the data for the cinema.
     *
     * @param rooms     A map of room IDs to Room objects.
     * @param movies    A list of Movie objects.
     * @param events    A list of CinemaEvent objects.
     * @param discounts A map of discount codes to their corresponding discount values.
     * @param snacks    A map of snack names to their prices.
     */
    void setData(Map<String, Room> rooms, List<Movie> movies, List<CinemaEvent> events, Map<Integer, Double> discounts, Map<String, Double> snacks) {
        this.rooms.putAll(rooms);
        this.movies.addAll(movies);
        this.events.addAll(events);
        this.discount.putAll(discounts);
        this.snacks.putAll(snacks);
    }

    @Override
    public Ticket book(CinemaEvent event, int row, int seat, int discountCode) throws AlreadyBookedException {
        if (!event.isValidSeat(row, seat)) {
            throw new IllegalArgumentException("Invalid seat!");
        }

        double discountValue = getDiscountValue(discountCode);

        return event.book(row, seat, discountValue);
    }

    @Override
    public Ticket refund(CinemaEvent selected, int row, int seat) throws NotBookedException {
        if (!selected.isValidSeat(row, seat)) {
            throw new IllegalStateException("Invalid seat!");
        }
        SeatId id = new SeatId(row, seat);
        Ticket removed = selected.getBookings().remove(id);
        if (removed == null) {
            throw new NotBookedException("Seat " + seat + " in row " + row + " is not booked yet!");
        }
        return removed;
    }

    protected double getDiscountValue(int code) {
        Double d = discount.get(code);
        if (d == null) {
            throw new IllegalArgumentException("Invalid discount code: " + code);
        }
        return d;
    }


    /**
     * old code
     */
    @SuppressWarnings("unused")
    public Room getRoom(String id) {
        if (id == null) return null;
        return rooms.get(id.toUpperCase(Locale.ROOT));
    }


    public int getAllSoldTickets() {
        return events.stream().mapToInt(e -> e.getTickets().size()).sum();
    }

    public double getAllSoldTicketsInPercent() {
        int sold = getAllSoldTickets();
        int capacityAcrossEvents = events.stream().mapToInt(e -> e.getRoom().getAllSeatsInHall()).sum();
        if (capacityAcrossEvents == 0) return 0.0;
        return (sold * 100.0) / capacityAcrossEvents;
    }


    public double getAllIncome() {
        return events.stream().flatMap(e -> e.getTickets().stream()).mapToDouble(Ticket::price).sum() + getAllSnackPrice();
    }

    public double getAllAverageIncome() {
        int sold = getAllSoldTickets();
        if (sold == 0) return 0.0;
        return getAllIncome() / sold;
    }

    public double getAllSnackPrice() {
        return boughtSnacks.values().stream().mapToDouble(Double::doubleValue).sum();
    }


    public double getAllPotentialIncome() {
        return events.stream().mapToDouble(e -> e.getRoom().getAllSeatsInHall() * e.getRoom().getTicketPrice(1)).sum();
    }

    @Override
    public String getAllStatistics() {
        return new Statistics(getAllSoldTickets(), getAllSoldTicketsInPercent(), getAllIncome(), getAllAverageIncome(), getAllPotentialIncome()).toString();
    }

    @Override
    public String getPerRoomStatisticsTable() {
        Map<Room, String> idByRoom = new HashMap<>();

        for (Map.Entry<String, Room> e : rooms.entrySet()) {
            idByRoom.put(e.getValue(), e.getKey());
        }

        List<Room> sortedRooms = new ArrayList<>(rooms.values());
        sortedRooms.sort(Comparator.comparingDouble((Room r) -> r.getStatistics().percentSold()).reversed().thenComparing(Room::getMovieName));

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

            sb.append(String.format("%s | %s | %d/%d | %.2f %%| $%.2f | $%.2f\n", roomId, room.getMovieName(), sold, total, percent, income, potential));
        }
        return sb.toString();
    }

    @SuppressWarnings("unused")
    public void addEvent(CinemaEvent event) {
        if (!events.contains(event)) {
            events.add(event);
        } else {
            System.out.println("This event has already been created!");
        }
    }

    @Override
    public List<CinemaEvent> getEvents() {
        return Collections.unmodifiableList(events);
    }


    //get events of the next dates
    @Override
    public List<CinemaEvent> getUpcomingEvents(LocalDateTime from, LocalDateTime to) {
        List<CinemaEvent> upcomingEvents = new ArrayList<>();
        for (CinemaEvent event : events) {
            LocalDateTime eventTime = event.getStartTime();
            if ((eventTime.equals(from) || eventTime.isAfter(from)) && (eventTime.equals(to) || eventTime.isBefore(to))) {
                upcomingEvents.add(event);
            }
        }
        return upcomingEvents;
    }

    public void addSnack(String name, double price) {
        snacks.put(name, price);
    }

    public Map<Object, Object> getSnacks() {
        return Collections.unmodifiableMap(snacks);
    }

    public void printSnackMenu() {
        int counter = 1;
        for (Map.Entry<String, Double> entry : snacks.entrySet()) {
            System.out.printf("%d. %s: $%.2f%n", counter++, entry.getKey(), entry.getValue());
        }
        System.out.println("0. Cancel");
    }

    public void buySnack(String name) {
        Double price = snacks.get(name);
        if (price != null) {
            boughtSnacks.put(name, price);
            System.out.printf("Bought snack: %s for $%.2f%n", name, price);
        } else {
            System.out.println("Snack not found: " + name);
        }
    }
}

package com.egger.cinema;

import java.time.LocalDateTime;
import java.util.*;


public class Cinema {
    private final Map<String, Room> rooms;
    private final Movies movies;
    private final List<CinemaEvent> events;
    private final Map<String, Double> snacks;
    private final Map<String, Double> boughtSnacks;
    private final Map<Integer, Double> discount;

    public Cinema() {
        this.snacks = new LinkedHashMap<>();
        this.movies = Movies.fromJsonResource("/movies.json");
        this.rooms = new TreeMap<>();
        this.events = new ArrayList<>();
        this.discount = new HashMap<>();
        this.boughtSnacks = new LinkedHashMap<>();

        List<Movie> list = movies.all();
        Movie m1 = randomlySelectMovie(list);
        Movie m2 = randomlySelectMovie(list);
        Movie m3 = randomlySelectMovie(list);
        Movie m4 = randomlySelectMovie(list);

        CinemaEvent e1 = new CinemaEvent(m1, null, LocalDateTime.now().plusHours(2));
        CinemaEvent e2 = new CinemaEvent(m2, null, LocalDateTime.now().plusHours(3));
        CinemaEvent e3 = new CinemaEvent(m3, null, LocalDateTime.now().plusHours(4));
        CinemaEvent e4 = new CinemaEvent(m4, null, LocalDateTime.now().plusHours(5));

        Room a = new Room(m1, 10, 7, "A", e1);
        Room b = new Room(m2, 5, 5, "B", e2);
        Room c = new Room(m3, 10, 10, "C", e3);
        Room d = new Room(m4, 5, 10, "D", e4);

        e1.setRoom(a);
        e2.setRoom(b);
        e3.setRoom(c);
        e4.setRoom(d);

        rooms.put("A", a);
        rooms.put("B", b);
        rooms.put("C", c);
        rooms.put("D", d);

        events.addAll(List.of(e1, e2, e3, e4));

        DiscountCode d1 = new DiscountCode(1, 0.0);
        DiscountCode d2 = new DiscountCode(2, 2.99);
        DiscountCode d3 = new DiscountCode(3, 1.99);
        DiscountCode d4 = new DiscountCode(4, 1.49);

        discount.putAll(Map.of(d1.code(), d1.discount(), d2.code(), d2.discount(), d3.code(), d3.discount(), d4.code(), d4.discount()));
    }

    @SuppressWarnings("unused")
    public Room getRoom(String id) {
        if (id == null) return null;
        return rooms.get(id.toUpperCase(Locale.ROOT));
    }

    public Ticket book(CinemaEvent event, int row, int seat, int discountCode) throws AlreadyBookedException {
        if (!event.isValidSeat(row, seat)) {
            throw new IllegalArgumentException("Invalid seat!");
        }

        double discountValue = getDiscountValue(discountCode);

        return event.book(row, seat, discountValue);
    }


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

    private double getDiscountValue(int code) {
        Double d = discount.get(code);
        if (d == null) {
            throw new IllegalArgumentException("Invalid discount code: " + code);
        }
        return d;
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

    public String getAllStatistics() {
        return new Statistics(getAllSoldTickets(), getAllSoldTicketsInPercent(), getAllIncome(), getAllAverageIncome(), getAllPotentialIncome()).toString();
    }


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

    public List<CinemaEvent> getEvents() {
        return Collections.unmodifiableList(events);
    }

    public Movie randomlySelectMovie(List<Movie> movies) {
        Random random = new Random();
        int index = random.nextInt(movies.size());
        return movies.get(index);
    }

    public void printAdminView() {
        System.out.println("Available Movies:");
        for (Movie movie : movies.getAll()) {
            System.out.printf("Title: %s, Genre: %s, Duration: %d minutes, Rating: %.1f, Base Price: $%.2f%n%n", movie.title(), movie.genre(), movie.durationMinutes(), movie.rating(), movie.basePrice());
        }
    }

    public boolean authenticateAdmin(String username, String password) {
        final String ADMIN_USERNAME = "admin";
        final String ADMIN_PASSWORD = "password123";

        return ADMIN_USERNAME.equals(username) && ADMIN_PASSWORD.equals(password);
    }

    //get events of the next dates

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
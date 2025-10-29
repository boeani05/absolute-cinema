package com.egger.cinema;

import java.util.Collection;
import java.util.Collections;

public class MegaPlexx extends AbstractCinema implements IAdminSupport {
    private final static String ADMIN_USERNAME = "admin";
    private final static String ADMIN_PASSWORD = "password123";

    public MegaPlexx() {
        super();
    }

    @Override
    public String getName() {
        return "MegaPlexx";
    }

    @Override
    public Collection<CinemaEvent> getAllEvents() {
        return Collections.unmodifiableList(events);
    }

    @Override
    public boolean isAdminAccess(String admin123, String password123) {
        if (admin123.equals("admin") && password123.equals("password123")) {
            return true;
        }
        return false;
    }

    @Override
    protected void loadData() {
        Mockdata.loadData(this);
    }

    @Override
    public boolean authenticateAdmin(String username, String password) {
        return ADMIN_USERNAME.equals(username) && ADMIN_PASSWORD.equals(password);
    }

    @Override
    public void printAdminView() {
        System.out.println("Available Movies:");
        for (Movie movie : movies) {
            System.out.printf("Title: %s, Genre: %s, Duration: %d minutes, Rating: %.1f, Base Price: $%.2f%n%n", movie.title(), movie.genre(), movie.durationMinutes(), movie.rating(), movie.basePrice());
        }
    }
}

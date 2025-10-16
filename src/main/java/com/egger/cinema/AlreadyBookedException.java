package com.egger.cinema;

public class AlreadyBookedException extends Exception {
    public AlreadyBookedException(String message) {
        super(message);
    }
}

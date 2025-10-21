package com.egger.cinema;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

public class Main {
    private Cinema megaplexx;
    private Scanner scanner;

    public static void main(String[] args) {
        Main main = new Main();
        main.start();
    }

    private void start() {
        scanner = new Scanner(System.in);
        megaplexx = new Cinema();

        System.out.println("Welcome to Megaplexx!");

        while (true) {
            System.out.println("""
                    1. Show all events
                    2. Buy ticket for an event
                    3. Refund ticket
                    4. Show events by date
                    5. Statistics
                    0. Exit
                    """);
            int choice = scanner.nextInt();

            while (choice != 0) {
                switch (choice) {
                    case 1:
                        showAllEvents();
                        break;
                    case 2:
                        buyTicketForEvent();
                        break;
                    case 3:
                        refundTicketForEvent();
                        break;
                    case 4:
                        showEventsByDate();
                        break;
                    case 5:
                        printStatistics();
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
                System.out.println("""
                        1. Show all events
                        2. Buy ticket for an event
                        3. Refund ticket
                        4. Show events by date
                        5. Statistics
                        0. Exit
                        """);
                choice = scanner.nextInt();
            }
            return;
        }

    }

    private void printStatistics() {

        System.out.println("1. Show statistics of whole cinema\n2. Show statistics in a per-room table");
        int choiceStatistics = scanner.nextInt();

        switch (choiceStatistics) {
            case 1:
                System.out.println(megaplexx.getAllStatistics());
                break;
            case 2:
                System.out.println(megaplexx.getPerRoomStatisticsTable());
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }


    private void buyTicketForEvent() {
        int eventToBuy;
        int rowNumberToBuy;
        int seatNumberToBuy;
        int amountToBuy;
        int discountCode;
        int counter;
        List<CinemaEvent> sorted = new ArrayList<>(megaplexx.getEvents());
        sorted.sort(Comparator.comparing(CinemaEvent::getStartTime));
        counter = 1;

        if (sorted.isEmpty()) {
            System.out.println("No events registered!");
            return;
        }


        for (CinemaEvent event : sorted) {
            System.out.println(counter++ + ". " + event.toString() + "\n");
        }

        System.out.println("Which event would you like to book a seat for?");
        eventToBuy = scanner.nextInt();

        while (eventToBuy < 1 || eventToBuy > sorted.size()) {
            System.out.println("Please enter a valid event!");
            eventToBuy = scanner.nextInt();
        }

        CinemaEvent selectedEvent = sorted.get(eventToBuy - 1);

        Room room = selectedEvent.getRoom();


        System.out.println("How many tickets do you want to buy?");
        amountToBuy = scanner.nextInt();

        while (amountToBuy > room.getAllSeatsInHall()) {
            System.out.println("This cinema hall doesn't support this much seats!\nEnter again: ");
            amountToBuy = scanner.nextInt();
        }

        for (int ticketNr = 1; ticketNr <= amountToBuy; ticketNr++) {
            System.out.println("What kind of ticket(s) do you want to buy?\n1. Regular\n2. Child\n3. Senior\n4. Student\n0. Cancel");
            discountCode = scanner.nextInt();

            if (discountCode == 0) {
                System.out.println("Transaction cancelled.");
                return;
            }

            while (discountCode < 1 || discountCode > 4) {
                System.out.println("Invalid input, enter again: ");
                discountCode = scanner.nextInt();
            }

            while (true) {
                System.out.println("\nEnter a row number:");
                rowNumberToBuy = scanner.nextInt();
                System.out.println("Enter a seat number in that row:");
                seatNumberToBuy = scanner.nextInt();

                if (!selectedEvent.isValidSeat(rowNumberToBuy, seatNumberToBuy)) {
                    System.out.println("\nWrong input!");
                    continue;
                }

                if (!selectedEvent.isSeatAvailable(rowNumberToBuy, seatNumberToBuy)) {
                    System.out.println("\nThat ticket has already been purchased!");
                    continue;
                }

                break;
            }


            try {
                Ticket boughtTicket = selectedEvent.book(rowNumberToBuy, seatNumberToBuy, discountCode);
                System.out.println("Ticket price: $" + String.format("%.2f", boughtTicket.price()));
            } catch (AlreadyBookedException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void refundTicketForEvent() {
        List<CinemaEvent> sorted = new ArrayList<>(megaplexx.getEvents());
        sorted.sort(Comparator.comparing(CinemaEvent::getStartTime));

        if (sorted.isEmpty()) {
            System.out.println("No events registered!");
            return;
        }

        int counter = 1;
        for (CinemaEvent e : sorted) {
            System.out.println(counter++ + ". " + e.toString());
        }

        System.out.println("What event would you like to refund?");

        int choice = scanner.nextInt();

        while (choice < 1 || choice > sorted.size()) {
            System.out.println("Please enter a valid event!");
            choice = scanner.nextInt();
        }

        CinemaEvent selected = sorted.get(choice - 1);

        System.out.println("Enter a row:");
        int rowToRefund = scanner.nextInt();
        System.out.println("Enter a seat:");
        int seatToRefund = scanner.nextInt();

        while (!selected.isValidSeat(rowToRefund, seatToRefund)) {
            System.out.println("Invalid input, enter again:");
            System.out.println("Enter a row:");
            rowToRefund = scanner.nextInt();
            System.out.println("Enter a seat:");
            seatToRefund = scanner.nextInt();
        }

        try {
            Ticket refunded = selected.refund(rowToRefund, seatToRefund);
            System.out.printf("Refunded: $%.2f%n", refunded.price());
        } catch (NotBookedException e) {
            System.out.println("Seat " + seatToRefund + " in row " + rowToRefund + " has not been booked yet!");
        }

    }

    public void showAllEvents() {
        List<CinemaEvent> sorted = new ArrayList<>(megaplexx.getEvents());
        sorted.sort(Comparator.comparing(CinemaEvent::getStartTime));

        LocalDate currentDate = null;

        for (CinemaEvent e : sorted) {
            LocalDate eventDate = e.getStartTime().toLocalDate();

            if (!eventDate.equals(currentDate)) {
                currentDate = eventDate;
                System.out.println("\n📅 " + eventDate);
            }

            System.out.println(" " + e + "\n");
        }
    }

    public void showEventsByDate() {
        System.out.println("Enter date (YYYY-MM-DD): ");
        String dateInput = scanner.next();

        try {
            LocalDate targetDate = LocalDate.parse(dateInput);

            List<CinemaEvent> filtered = megaplexx.getEvents().stream()
                    .filter(e -> e.getStartTime().toLocalDate().equals(targetDate))
                    .sorted(Comparator.comparing(CinemaEvent::getStartTime))
                    .toList();

            if (filtered.isEmpty()) {
                System.out.println("No events found for this date!");
            } else {
                System.out.println("\n📅 " + targetDate);
                for (CinemaEvent e : filtered) {
                    System.out.println(" " + e);
                }
            }
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format! Please use YYYY-MM-DD");
        }
    }
}
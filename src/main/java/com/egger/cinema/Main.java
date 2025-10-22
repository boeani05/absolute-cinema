package com.egger.cinema;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

        megaplexx.addSnack("Popcorn", 5.00);
        megaplexx.addSnack("Soda", 3.00);
        megaplexx.addSnack("Candy", 4.00);

        System.out.println("Welcome to Megaplexx!");

        System.out.println("""
                
                1. Show all events
                2. Show rooms for event
                3. Buy ticket for an event
                4. Buy snacks
                5. Refund ticket
                6. Show events by date
                7. Statistics
                8. Admin Panel
                9. Show your tickets
                0. Exit
                """);

        String choiceStr;

        while (!scanner.hasNextInt()) {
            System.out.println("Please enter a valid number");
            scanner.next();
        }
        choiceStr = scanner.next();
        int choice = Integer.parseInt(choiceStr);


        while (choice != 0) {
            switch (choice) {
                case 1:
                    showAllEvents();
                    break;
                case 2:
                    showRoomsForEvent();
                    break;
                case 3:
                    buyTicketForEvent();
                    break;
                case 4:
                    buySnacks();
                    break;
                case 5:
                    refundTicketForEvent();
                    break;
                case 6:
                    showEventsByDate();
                    break;
                case 7:
                    printStatistics();
                    break;
                case 8:
                    showAdminPanel();
                    break;
                case 9:
                    showAllTicketsPerEvent();
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
            System.out.println("""
                    
                    1. Show all events
                    2. Show rooms for event
                    3. Buy ticket for an event
                    4. Buy snacks
                    5. Refund ticket
                    6. Show events by date
                    7. Statistics
                    8. Admin Panel
                    9. Show your tickets
                    0. Exit
                    """);

            while (!scanner.hasNextInt()) {
                System.out.println("Please enter a valid number");
                scanner.next();
            }
            choiceStr = scanner.next();
            choice = Integer.parseInt(choiceStr);
        }
        System.out.println("Thank you for visiting Megaplexx! Goodbye!");
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

        System.out.println("\n📅" + LocalDate.now() + "\n");
        for (CinemaEvent event : sorted) {
            System.out.println(counter++ + ". " + event.toString() + "\n");
        }

        System.out.println("Which event would you like to book a seat for?");
        try {
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
                System.out.println("This cinema hall doesn't support this much seats!\n\nEnter again: ");
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
                    System.out.println("\nEnter a row number (1 - " + room.getRowsInHall() + "):");
                    rowNumberToBuy = scanner.nextInt();
                    System.out.println("Enter a seat number in that row (1 - " + room.getSeatsPerRow() + "):");
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
        } catch (InputMismatchException e) {
            System.out.println("Please enter something valid!");
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
        System.out.println("\n📅" + LocalDate.now() + "\n");
        for (CinemaEvent e : sorted) {
            System.out.println(counter++ + ". " + e.toString() + "\n");
        }

        System.out.println("What event would you like to refund?");

        int choice = scanner.nextInt();

        while (choice < 1 || choice > sorted.size()) {
            System.out.println("Please enter a valid event!");
            choice = scanner.nextInt();
        }

        CinemaEvent selected = sorted.get(choice - 1);

        Room room = selected.getRoom();
        room.printSeatingChart();

        System.out.println("\nEnter a row:");
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
        int counter = 1;
        List<CinemaEvent> sorted = new ArrayList<>(megaplexx.getEvents());
        sorted.sort(Comparator.comparing(CinemaEvent::getStartTime));

        LocalDate currentDate = null;

        for (CinemaEvent e : sorted) {
            LocalDate eventDate = e.getStartTime().toLocalDate();

            if (!eventDate.equals(currentDate)) {
                currentDate = eventDate;
                System.out.println("\n📅 " + eventDate + "\n");
            }

            System.out.println(counter++ + ". " + e + "\n");
        }
    }

    public void showEventsByDate() {
        System.out.println("Enter date (YYYY-MM-DD):");
        String dateInput = scanner.next();

        LocalDate date;
        try {
            date = LocalDate.parse(dateInput);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format!");
            return;
        }

        List<CinemaEvent> eventsOnDate = new ArrayList<>();
        for (CinemaEvent event : megaplexx.getEvents()) {
            if (event.getStartTime().toLocalDate().equals(date)) {
                eventsOnDate.add(event);
            }
        }

        //TODO: add events of the entered date

        for (CinemaEvent event : megaplexx.getUpcomingEvents(LocalDateTime.now(), LocalDateTime.now().plusDays(7))) {
            if (event.getStartTime().toLocalDate().equals(date)) {
                eventsOnDate.add(event);
            }
        }


        if (eventsOnDate.isEmpty()) {
            System.out.println("No events found on " + date);
            return;
        }

        eventsOnDate.sort(Comparator.comparing(CinemaEvent::getStartTime));
        int counter = 1;

        System.out.println("\n📅 " + date + "\n");

        for (CinemaEvent event : eventsOnDate) {
            System.out.println(counter++ + ". " + event + "\n");
        }
    }

    public void showRoomsForEvent() {
        List<CinemaEvent> sorted = new ArrayList<>(megaplexx.getEvents());
        sorted.sort(Comparator.comparing(CinemaEvent::getStartTime));
        int counter = 1;

        System.out.println("\n📅 " + LocalDate.now() + "\n");

        for (CinemaEvent event : sorted) {
            System.out.println(counter++ + ". " + event.toString() + "\n");
        }

        System.out.println("Which event would you like to see the rooms for?");

        String eventToShowStr;

        while (!scanner.hasNextInt()) {
            System.out.println("Please enter a valid number");
            scanner.next();
        }
        eventToShowStr = scanner.next();
        int eventToShow = Integer.parseInt(eventToShowStr);


        while (eventToShow < 1 || eventToShow > sorted.size()) {
            System.out.println("Please enter a valid event!");
            eventToShow = scanner.nextInt();
        }

        CinemaEvent selectedEvent = sorted.get(eventToShow - 1);
        Room room = selectedEvent.getRoom();
        room.printSeatingChart();
    }

    public void showAdminPanel() {
        System.out.println("Enter username:");
        String username = scanner.next();
        System.out.println("Enter password:");
        String password = scanner.next();
        if (!megaplexx.authenticateAdmin(username, password)) {
            System.out.println("Invalid credentials!");
            return;
        }
        megaplexx.printAdminView();
    }

    public void buySnacks() {
        int snackChoice;

        System.out.println("What snack would you like to buy?");
        megaplexx.printSnackMenu();
        snackChoice = scanner.nextInt();

        while (snackChoice < 1 || snackChoice > megaplexx.getSnacks().size()) {
            System.out.println("Invalid choice, enter again:");
            snackChoice = scanner.nextInt();
        }
        megaplexx.buySnack((String) megaplexx.getSnacks().keySet().toArray()[snackChoice - 1]);
    }

    public void showAllTicketsPerEvent() {
        for (CinemaEvent event : megaplexx.getEvents()) {
            int soldTicketsCounter = event.getSoldTickets();
            System.out.println("Tickets for event " + event.getRoom().getRoomId() + ": " + soldTicketsCounter + " sold tickets.");
            System.out.println();
        }
    }
}
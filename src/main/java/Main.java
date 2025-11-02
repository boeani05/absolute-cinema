import com.egger.cinema.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

public class Main {
    private Scanner scanner;

    private ICinema chosenCinema;

    public static void main(String[] args) {
        Main main = new Main();
        main.start();
    }

    private void start() {
        scanner = new Scanner(System.in);
        Map<String, ICinema> cinemas = new HashMap<>();

        cinemas.put("megaplexx", new MegaPlexx());
        cinemas.put("cineplexx", new CinePlexx());


        System.out.println("1.) Megaplexx\n2.) Cineplexx");
        int choice;
        while (true) {
            try {
                choice = scanner.nextInt();
                switch (choice) {
                    case 1 -> chosenCinema = cinemas.get("chosenCinema");
                    case 2 -> chosenCinema = cinemas.get("cineplexx");
                    default -> {
                        System.out.println("Please enter a valid number!");
                        continue;
                    }
                }
                break;
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid number!");
                scanner.next();
            }
        }

        System.out.println("Welcome to " + chosenCinema.getName() + "!");

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

        while (true) {
            try {
                choice = scanner.nextInt();
                break;
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid number!");
                scanner.next();
            }
        }


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

            while (true) {
                try {
                    choice = scanner.nextInt();
                    break;
                } catch (InputMismatchException e) {
                    System.out.println("Please enter a valid number!");
                    scanner.next();
                }
            }
        }
        System.out.println("Thank you for visiting chosenCinema! Goodbye!");
    }

    private void printStatistics() {

        System.out.println("1. Show statistics of whole cinema\n2. Show statistics in a per-room table");
        int choiceStatistics;

        while (true) {
            try {
                choiceStatistics = scanner.nextInt();
                break;
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid number:");
                scanner.next();
            }
        }

        switch (choiceStatistics) {
            case 1:
                System.out.println(chosenCinema.getAllStatistics());
                break;
            case 2:
                System.out.println(chosenCinema.getPerRoomStatisticsTable());
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
        List<CinemaEvent> sorted = new ArrayList<>(chosenCinema.getEvents());
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

        CinemaEvent selectedEvent;

        while (true) {
            try {
                eventToBuy = scanner.nextInt();
                selectedEvent = sorted.get(eventToBuy - 1);
                break;
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid number!");
                scanner.next();
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Please enter a number from 1 - 4!");
            }
        }


        Room room = selectedEvent.getRoom();

        System.out.println("How many tickets do you want to buy?");

        while (true) {
            try {
                amountToBuy = scanner.nextInt();
                while (amountToBuy > room.getAllSeatsInHall()) {
                    System.out.println("The room for this event doesn't support that much seats.\nPlease enter again:");
                    amountToBuy = scanner.nextInt();
                }
                break;
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid number!");
                scanner.next();
            }
        }

        for (int i = 1; i <= amountToBuy; i++) {
            System.out.println("What kind of ticket do you want to buy?\n1. Regular\n2. Child\n3. Senior\n4. Student\n0. Cancel");

            while (true) {
                try {
                    discountCode = scanner.nextInt();

                    if (discountCode == 0) {
                        return;
                    }

                    while (discountCode < 1 || discountCode > 4) {
                        System.out.println("Invalid input, enter again: ");
                        discountCode = scanner.nextInt();
                    }
                    break;
                } catch (InputMismatchException e) {
                    System.out.println("Please enter a valid number:");
                    scanner.next();
                }
            }


            room.printSeatingChart();

            while (true) {
                System.out.println("\nEnter the row and seat you would like to book for this event:");
                while (true) {
                    try {
                        rowNumberToBuy = scanner.nextInt();
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
                    } catch (InputMismatchException e) {
                        System.out.println("Please enter a valid number!");
                        scanner.next();
                    }
                }
                break;
            }
            try {
                Ticket boughtTicket = chosenCinema.book(selectedEvent, rowNumberToBuy, seatNumberToBuy, discountCode);
                System.out.println("Ticket price: $" + String.format("%.2f", boughtTicket.price()));
            } catch (AlreadyBookedException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void refundTicketForEvent() {
        List<CinemaEvent> sorted = new ArrayList<>(chosenCinema.getEvents());
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

        int choice;
        while (true) {
            try {
                choice = scanner.nextInt();
                while (choice < 1 || choice > sorted.size()) {
                    System.out.println("Please enter a valid event!");
                    choice = scanner.nextInt();
                }
                break;
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid number:");
                scanner.next();
            }
        }


        CinemaEvent selected = sorted.get(choice - 1);

        Room room = selected.getRoom();
        room.printSeatingChart();

        int rowToRefund;
        int seatToRefund;

        while (true) {
            try {
                System.out.println("\nEnter a row:");
                rowToRefund = scanner.nextInt();
                System.out.println("Enter a seat:");
                seatToRefund = scanner.nextInt();

                while (!selected.isValidSeat(rowToRefund, seatToRefund)) {
                    System.out.println("Invalid input, enter again:");
                    System.out.println("Enter a row:");
                    rowToRefund = scanner.nextInt();
                    System.out.println("Enter a seat:");
                    seatToRefund = scanner.nextInt();
                }
                break;
            } catch (InputMismatchException e) {
                System.out.println("Please enter valid numbers:");
                scanner.next();
            }
        }


        try {
            Ticket refunded = chosenCinema.refund(selected, rowToRefund, seatToRefund);
            System.out.printf("Refunded: $%.2f%n", refunded.price());
        } catch (NotBookedException e) {
            System.out.println("Seat " + seatToRefund + " in row " + rowToRefund + " has not been booked yet!");
        }

    }

    public void showAllEvents() {
        int counter = 1;
        List<CinemaEvent> sorted = new ArrayList<>(chosenCinema.getEvents());
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
        for (CinemaEvent event : chosenCinema.getEvents()) {
            if (event.getStartTime().toLocalDate().equals(date)) {
                eventsOnDate.add(event);
            }
        }

        //TODO: add events of the entered date

        for (CinemaEvent event : chosenCinema.getUpcomingEvents(LocalDateTime.now(), LocalDateTime.now().plusDays(7))) {
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
        List<CinemaEvent> sorted = new ArrayList<>(chosenCinema.getEvents());
        sorted.sort(Comparator.comparing(CinemaEvent::getStartTime));
        int counter = 1;

        System.out.println("\n📅 " + LocalDate.now() + "\n");

        for (CinemaEvent event : sorted) {
            System.out.println(counter++ + ". " + event.toString() + "\n");
        }

        System.out.println("Which event would you like to see the rooms for?");


        int eventToShow;
        CinemaEvent selectedEvent;
        while (true) {
            try {
                eventToShow = scanner.nextInt();
                selectedEvent = sorted.get(eventToShow - 1);
                break;
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid number!");
                scanner.next();
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Please enter a number from 1 - 4!");
            }
        }


        Room selectedRoom = selectedEvent.getRoom();

        selectedRoom.printSeatingChart();
    }

    public void showAdminPanel() {
        if (chosenCinema instanceof IAdminSupport adminSupport) {
            System.out.println("Enter username:");
            String username = scanner.next();
            System.out.println("Enter password:");
            String password = scanner.next();
            if (!adminSupport.authenticateAdmin(username, password)) {
                System.out.println("Invalid credentials!");
                return;
            }
            adminSupport.printAdminView();
        } else {
            System.out.println("No admin support for " + chosenCinema.getName());

        }

    }

    public void buySnacks() {
        System.out.println("What snack would you like to buy?");
        chosenCinema.printSnackMenu();

        int snackChoice;
        while (true) {
            try {
                snackChoice = scanner.nextInt();
                if (snackChoice == 0) {
                    return;
                }

                if (snackChoice >= 1 && snackChoice <= chosenCinema.getSnacks().size()) {
                    break;
                }
                System.out.println("Invalid choice, enter again:");
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid number!");
                scanner.next();
            }
        }

        ArrayList<Object> namesInOrder = new ArrayList<>(chosenCinema.getSnacks().keySet());
        String chosen = namesInOrder.get(snackChoice - 1).toString();
        chosenCinema.buySnack(chosen);
    }


    public void showAllTicketsPerEvent() {
        for (CinemaEvent event : chosenCinema.getEvents()) {
            int soldTicketsCounter = event.getSoldTickets();
            System.out.println("Tickets for event " + event.getRoom().getRoomId() + ": " + soldTicketsCounter + " sold tickets.");
            System.out.println();
        }
    }
}
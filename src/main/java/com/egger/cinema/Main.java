package com.egger.cinema;

import java.util.Map;
import java.util.Scanner;

public class Main {
    private Cinema megaplexx;
    private Room selectedRoom;
    private Scanner scanner;

    public static void main(String[] args) {
        Main main = new Main();
        main.start();
    }

    private void start() {
        scanner = new Scanner(System.in);

//        System.out.println("Welcome to Megaplexx! What movie would you like to see?\nA Godzilla\n2. Dune\n3. Interstellar\n4. The Ring\n0. Exit");
        megaplexx = new Cinema();
        System.out.println("Welcome to Megaplexx! What movie would you like to see?\n");

        for(Map.Entry<String, Room> entry: megaplexx.getRooms().entrySet()){
            System.out.println(entry.getKey() + "\t" + entry.getValue().getMovieName() + "\n");
        }

        Room room;

        while (true) {
                String input = scanner.next();

                if("0".equals(input)){
                    return;
                }

                room = megaplexx.getRoom(input);
                if (room == null) {
                    System.out.println("Invalid input, try again: ");
                    continue;
                }
                break;
            }

        selectedRoom = room;


        System.out.println("1. Show the seats\n2. Buy a ticket\n3. Statistics\n0. Choose another movie");
        int choice = scanner.nextInt();

        while (true) {
            switch (choice) {
                case 1:
                    showSeats();
                    break;
                case 2:
                    buyTicket();
                    break;
                case 3:
                    printStatistics();
                    break;
                case 0:
                    start();
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
            System.out.println("\n1. Show the seats\n2. Buy a ticket\n3. Statistics\n0. Choose another movie");
            choice = scanner.nextInt();
        }
    }

    private void printStatistics() {

        System.out.println("1. Show statistics of current movie hall\n2. Show statistics of all movie halls");
        int choiceStatistics = scanner.nextInt();

        switch (choiceStatistics) {
            case 1:
                Statistics statistics = selectedRoom.getStatistics();

                System.out.println("Number of purchased tickets: " + statistics.soldTicketAmount());
                System.out.println("Percentage: " + String.format("%.2f", statistics.percentSold()) + "%");
                System.out.println("Current  income: $" + statistics.income());
                System.out.println("Total potential income: $" + String.format("%.2f", statistics.maxIncome()));
                return;
            case 2:
                System.out.println("All-halls statistics coming soon!");
                return;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    private void showSeats() {
        System.out.println("\nCinema:");

        for (int i = 0; i <= selectedRoom.getRowsInHall(); i++) {
            for (int j = 0; j <= selectedRoom.getSeatsPerRow(); j++) {
                if (i == 0 && j == 0) {
                    System.out.print("  ");
                } else if (i == 0) {
                    System.out.print(j + " ");
                } else if (j == 0) {
                    System.out.print(i + " ");
                } else if (selectedRoom.isSeatAvailable(i, j)) {
                    System.out.print("S ");
                } else {
                    System.out.print("B ");
                }
            }
            System.out.println();
        }
    }


    private void buyTicket() {
        int rowNumberToBuy;
        int seatNumberToBuy;
        int amountToBuy;
        int discountCode;


        System.out.println("How many tickets do you want to buy?");
        amountToBuy = scanner.nextInt();

        while (amountToBuy > selectedRoom.getAllSeatsInHall()) {
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

                if (!selectedRoom.isValidSeat(rowNumberToBuy, seatNumberToBuy)) {
                    System.out.println("\nWrong input!");
                    continue;
                }

                if (!selectedRoom.isSeatAvailable(rowNumberToBuy, seatNumberToBuy)) {
                    System.out.println("\nThat ticket has already been purchased!");
                    continue;
                }

                break;
            }


            try {
                Ticket boughtTicket = selectedRoom.book(rowNumberToBuy, seatNumberToBuy, discountCode);
                System.out.println("Ticket price: $" + String.format("%.2f", boughtTicket.price()));
            } catch (AlreadyBookedException e) {
                System.out.println(e.getMessage());
            }
        }
    }

}
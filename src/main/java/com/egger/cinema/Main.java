package com.egger.cinema;

import java.util.Scanner;

public class Main {

    private Cinema megaplexx;
    private Scanner scanner;

    public static void main(String[] args) {
        Main main = new Main();
        main.start();
    }

    private void start() {
        scanner = new Scanner(System.in);

        int rows = 9;
        int seatsPerRow = 10;

        megaplexx = new Cinema(seatsPerRow, rows);

        System.out.println("Welcome to Megaplexx!\n1. Show the seats\n2. Buy a ticket\n3. Statistics\n0. Exit");
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
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
            System.out.println("\n1. Show the seats\n2. Buy a ticket\n3. Statistics\n0. Exit");
            choice = scanner.nextInt();
        }
    }

    private void printStatistics() {
        Statistics statistics = megaplexx.getStatistics();

        System.out.println("Number of purchased tickets: " + statistics.soldTicketAmount());
        System.out.println("Percentage: " + String.format("%.2f", statistics.percentSold()) + "%");
        System.out.println("Current  income: $" + statistics.income());
        System.out.println("Total income: $" + statistics.maxIncome());
    }

    private void showSeats() {
        System.out.println("\nCinema:");

        for (int i = 0; i <= megaplexx.getRowsInCinema(); i++) {
            for (int j = 0; j <= megaplexx.getSeatsInRow(); j++) {
                if (i == 0 && j == 0) {
                    System.out.print("  ");
                } else if (i == 0) {
                    System.out.print(j + " ");
                } else if (j == 0) {
                    System.out.print(i + " ");
                } else if (megaplexx.isSeatAvailable(i, j)) {
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
        int kindOfTicket;



        System.out.println("How many tickets do you want to buy?");
        amountToBuy = scanner.nextInt();

        while (amountToBuy > megaplexx.getTotalSeatsInCinema()) {
            System.out.println("This cinema hall doesn't support this much seats!\nEnter again: ");
            amountToBuy = scanner.nextInt();
        }

        for (int ticketNr = 1; ticketNr <= amountToBuy; ticketNr++) {
            System.out.println("What kind of ticket(s) do you want to buy?\n1. Regular\n2. Child\n3. Senior\n4. Student");
            kindOfTicket = scanner.nextInt();

            while (kindOfTicket < 1 || kindOfTicket > 4) {
                System.out.println("Invalid input, enter again: ");
                kindOfTicket = scanner.nextInt();
            }

            while (true) {
                System.out.println("\nEnter a row number:");
                rowNumberToBuy = scanner.nextInt();
                System.out.println("Enter a seat number in that row:");
                seatNumberToBuy = scanner.nextInt();

                if (!megaplexx.isSeatValid(rowNumberToBuy, seatNumberToBuy)) {
                    System.out.println("\nWrong input!");
                    continue;
                }

                if (!megaplexx.isSeatAvailable(rowNumberToBuy, seatNumberToBuy)) {
                    System.out.println("\nThat ticket has already been purchased!");
                    continue;
                }

                break;
            }
            System.out.println("\nTicket price: $" + megaplexx.getTicketPrice(rowNumberToBuy, kindOfTicket));

            megaplexx.bookSeat(rowNumberToBuy, seatNumberToBuy, kindOfTicket);
        }
    }
}

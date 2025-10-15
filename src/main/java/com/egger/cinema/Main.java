package com.egger.cinema;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {

    private final Cinema godzillaMovie = new Cinema(10, 7);
    private final Cinema duneMovie = new Cinema(5, 5);
    private final Cinema interstellarMovie = new Cinema(10, 10);
    private final Cinema ringMovie = new Cinema(5, 10);
    private final Cinema allStatistics = new Cinema(0, 0);
    private Cinema selectedMovie;
    private Scanner scanner;

    public static void main(String[] args) {
        Main main = new Main();
        main.start();
    }

    private void start() {
        scanner = new Scanner(System.in);

        System.out.println("Welcome to Megaplexx! What movie would you like to see?\n1. Godzilla\n2. Dune\n3. Interstellar\n4. The Ring\n0. Exit");

        int chooseMovie;
        while (true) {
            try {
                chooseMovie = scanner.nextInt();
                break;
            } catch (InputMismatchException e) {
                System.out.println("Please enter a number: ");
                scanner.next();
            }
        }

        while (chooseMovie < 0 || chooseMovie > 4) {
            System.out.println("Invalid input, try again: ");
            chooseMovie = scanner.nextInt();
        }

        switch (chooseMovie) {
            case 1:
                selectedMovie = godzillaMovie;
                break;
            case 2:
                selectedMovie = duneMovie;
                break;
            case 3:
                selectedMovie = interstellarMovie;
                break;
            case 4:
                selectedMovie = ringMovie;
                break;
            case 0:
                System.out.println("Thank you for visiting Megaplexx!");
                System.exit(0);
        }



        System.out.println("1. Show the seats\n2. Buy a ticket\n3. Statistics\n0. Choose another movie");
        int choice = scanner.nextInt();

        while (true) {
            switch (choice) {
                case 1:
                    showSeats();
                    break;
                case 2:
                    buyTicket(chooseMovie);
                    break;
                case 3:
                    printStatistics(chooseMovie);
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

    private void printStatistics(int chosenMovie) {

        System.out.println("1. Show statistics of current movie hall\n2. Show statistics of all movie halls");
        int choiceStatistics = scanner.nextInt();

        switch (choiceStatistics) {
            case 1:
                Statistics statistics = selectedMovie.getStatistics(chosenMovie);

                System.out.println("Number of purchased tickets: " + statistics.soldTicketAmount());
                System.out.println("Percentage: " + String.format("%.2f", statistics.percentSold()) + "%");
                System.out.println("Current  income: $" + statistics.income());
                System.out.println("Total income: $" + String.format("%.2f", statistics.maxIncome()));
                return;
            case 2:
                System.out.println("Number of purchased tickets: " + allStatistics.getAllSoldTickets(godzillaMovie, duneMovie, interstellarMovie, ringMovie));
                System.out.println("Percentage: " + String.format("%.2f", allStatistics.getAllPercentSold(godzillaMovie, duneMovie, interstellarMovie, ringMovie)) + "%");
                System.out.println("Current  income: $" + allStatistics.getAllIncome(godzillaMovie, duneMovie, interstellarMovie, ringMovie));
                System.out.println("Total income: $" + String.format("%.2f", allStatistics.getAllMaxIncome(godzillaMovie, duneMovie, interstellarMovie, ringMovie)));
                return;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    private void printMovieStatistics(Cinema currentMovie, int chosenMovie) {
        Statistics statistics = currentMovie.getStatistics(chosenMovie);

        System.out.println("Number of purchased tickets: " + statistics.soldTicketAmount());
        System.out.println("Percentage: " + String.format("%.2f", statistics.percentSold()) + "%");
        System.out.println("Current  income: $" + statistics.income());
        System.out.println("Total income: $" + String.format("%.2f", statistics.maxIncome()));
    }

    private void showSeats() {
        System.out.println("\nCinema:");

        for (int i = 0; i <= selectedMovie.getRowsInCinema(); i++) {
            for (int j = 0; j <= selectedMovie.getSeatsInRow(); j++) {
                if (i == 0 && j == 0) {
                    System.out.print("  ");
                } else if (i == 0) {
                    System.out.print(j + " ");
                } else if (j == 0) {
                    System.out.print(i + " ");
                } else if (selectedMovie.isSeatAvailable(i, j)) {
                    System.out.print("S ");
                } else {
                    System.out.print("B ");
                }
            }
            System.out.println();
        }
    }


    private void buyTicket(int movieChosen) {
        int rowNumberToBuy;
        int seatNumberToBuy;
        int amountToBuy;
        int kindOfTicket;



        System.out.println("How many tickets do you want to buy?");
        amountToBuy = scanner.nextInt();

        while (amountToBuy > selectedMovie.getTotalSeatsInCinema()) {
            System.out.println("This cinema hall doesn't support this much seats!\nEnter again: ");
            amountToBuy = scanner.nextInt();
        }

        for (int ticketNr = 1; ticketNr <= amountToBuy; ticketNr++) {
            System.out.println("What kind of ticket(s) do you want to buy?\n1. Regular\n2. Child\n3. Senior\n4. Student\n0. Cancel");
            kindOfTicket = scanner.nextInt();

            if (kindOfTicket == 0) {
                System.out.println("Transaction cancelled.");
                return;
            }

            while (kindOfTicket < 1 || kindOfTicket > 4) {
                System.out.println("Invalid input, enter again: ");
                kindOfTicket = scanner.nextInt();
            }

            while (true) {
                System.out.println("\nEnter a row number:");
                rowNumberToBuy = scanner.nextInt();
                System.out.println("Enter a seat number in that row:");
                seatNumberToBuy = scanner.nextInt();

                if (!selectedMovie.isSeatValid(rowNumberToBuy, seatNumberToBuy)) {
                    System.out.println("\nWrong input!");
                    continue;
                }

                if (!selectedMovie.isSeatAvailable(rowNumberToBuy, seatNumberToBuy)) {
                    System.out.println("\nThat ticket has already been purchased!");
                    continue;
                }

                break;
            }
            System.out.println("\nTicket price: $" + selectedMovie.getTicketPrice(kindOfTicket, movieChosen));

            selectedMovie.bookSeat(rowNumberToBuy, seatNumberToBuy, kindOfTicket);
        }
    }
}
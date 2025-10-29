package com.egger.cinema;

import org.junit.Before;
import org.junit.Test;

import java.util.Comparator;

import static org.junit.Assert.*;

public class CinemaTest {

    private AbstractCinema cinema;
    private CinemaEvent event;
    private int row;
    private int seat;

    @Before
    public void setUp() {
        cinema = new AbstractCinema() {
            @Override
            public String getName() {
                return "";
            }

            @Override
            protected void loadData() {

            }
        };

        event = cinema.getEvents().stream()
                .min(Comparator.comparing(CinemaEvent::getStartTime))
                .orElseThrow(() -> new AssertionError("No events initialized"));

        row = 1;
        seat = 1;

        assertTrue("Precondition: seat must be valid", event.isValidSeat(row, seat));
    }

    @Test
    public void bookSeatWithDiscount() throws Exception {
        int discountCode = 2;
        double discountValue = 2.99;

        double basePrice = event.getRoom().getTicketPrice(0);
        double realPrice = basePrice - discountValue;

        Ticket ticket = cinema.book(event, row, seat, discountCode);

        assertNotNull(ticket);
        System.out.println("Event used: " + event);
        assertEquals(realPrice, ticket.price(), 0.001);


        assertEquals(1, cinema.getAllSoldTickets());
        assertTrue("Average should be ticket price!",
                Math.abs(cinema.getAllAverageIncome() - ticket.price()) < 0.0001);
        assertTrue("Total income should be price + snacks",
                cinema.getAllIncome() >= ticket.price());
    }

    @Test(expected = AlreadyBookedException.class)
    public void bookedTwice() throws Exception {
        cinema.book(event, row, seat, 1);
        cinema.book(event, row, seat, 1);
    }

    @Test
    public void refundAndBookAfter() throws Exception {
        cinema.book(event, row, seat, 3);
        assertEquals(1, cinema.getAllSoldTickets());

        Ticket refunded = cinema.refund(event, row, seat);
        assertNotNull(refunded);
        assertEquals(0, cinema.getAllSoldTickets());

        Ticket buyTicket = cinema.book(event, row, seat, 4);
        assertNotNull(buyTicket);
        assertEquals(1, cinema.getAllSoldTickets());
    }

    @Test(expected = NotBookedException.class)
    public void refundNotBookedSeat() throws Exception {
        cinema.refund(event, row, seat);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidSeat() throws Exception {
        cinema.book(event, 999, 999, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidDiscountCode() throws Exception {
        cinema.book(event, 1, 1, 999);
    }

    @Test
    public void addSnacksToIncome() {
        double priceBeforeSnacks = cinema.getAllIncome();

        cinema.addSnack("Popcorn", 5.99);
        cinema.addSnack("Soda", 1.99);

        cinema.buySnack("Popcorn");
        cinema.buySnack("Soda");

        double snackIncome = cinema.getAllSnackPrice();
        assertEquals(7.98, snackIncome, 0.001);

        double incomeAfterSnacks = cinema.getAllIncome();
        assertEquals("Snack income should be summed up",
                priceBeforeSnacks + snackIncome, incomeAfterSnacks, 0.001);
    }

    @Test
    public void formatStatisticsAndTable() throws Exception {
        int soldStart = cinema.getAllSoldTickets();

        cinema.book(event, 1, 2, 1);
        cinema.book(event, 1, 3, 1);

        int sold = cinema.getAllSoldTickets() - soldStart;
        assertEquals(2, sold);

        double percent = cinema.getAllSoldTicketsInPercent();
        assertTrue("Percent sold should be > 0", percent > 0.0);

        double potentialIncome = cinema.getAllPotentialIncome();
        assertTrue("Potential income should be > 0", potentialIncome > 0.0);
        assertTrue("Income mustn't be greater that potential income", cinema.getAllIncome() <= potentialIncome);

        String table = cinema.getPerRoomStatisticsTable();
        assertTrue(table.contains("Room | Movie | Sold/Total | % Sold | Income | Potential"));
        assertTrue(table.contains("A") || table.contains("B") || table.contains("C") || table.contains("D"));

        String allStats = cinema.getAllStatistics();
        assertNotNull(allStats);
        assertFalse(allStats.trim().isEmpty());
    }


    // testdaten manuell erstellen

    //cinema.setData(null, null, null);

    // ein vorher erstelletes event wählen

    // eine buchung durchführen
    //cinema.book()

    // check

    // 1. doppelbuchung?
    // 2. statistiken?
    // 3. rabatte?

}

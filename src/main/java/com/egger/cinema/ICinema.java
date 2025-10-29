package com.egger.cinema;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * High-level interface that represents a cinema with events, booking and snack operations.
 *
 * <p>Implementations provide access to the cinema name, the list of available events and
 * methods to book and refund tickets. The interface also exposes simple snack-menu operations
 * and statistics reporting.</p>
 *
 * <p>Note: method behaviour such as time-range inclusivity or the exact Map contents for
 * {@link #getSnacks()} is implementation-defined and should be documented by concrete classes.</p>
 */
public interface ICinema {
    /**
     * Returns the name of this cinema.
     *
     * @return the cinema name (never null)
     */
    String getName();

    /**
     * Returns all known events for this cinema.
     *
     * @return an unmodifiable or defensive-copied list of {@link CinemaEvent} objects; never null
     */
    List<CinemaEvent> getEvents();

    /**
     * Returns events that take place between the given time range.
     *
     * <p>Implementations typically include events that start on or after {@code from} and
     * on or before {@code to}, but exact semantics (inclusive/exclusive) are implementation-defined.
     * Use concrete implementations' documentation for precise rules.</p>
     *
     * @param from lower bound of the time window (inclusive)
     * @param to   upper bound of the time window (inclusive)
     * @return list of {@link CinemaEvent} that occur within the given interval; never null
     * @throws IllegalArgumentException if {@code from} is after {@code to}
     */
    List<CinemaEvent> getUpcomingEvents(LocalDateTime from, LocalDateTime to);


    /**
     * Books a ticket for the given event and seat.
     *
     * @param event        the event to book a ticket for (must not be null)
     * @param row          seat row index (domain depends on implementation)
     * @param seat         seat index in the row (domain depends on implementation)
     * @param discountCode an integer that represents a discount; interpretation is implementation-defined
     * @return a {@link Ticket} representing the successful booking
     * @throws AlreadyBookedException if the requested seat is already booked
     * @throws IllegalArgumentException if {@code event} is null or seat indices are invalid
     */
    Ticket book(CinemaEvent event, int row, int seat, int discountCode) throws AlreadyBookedException;

    /**
     * Refunds a previously booked ticket for the given event and seat.
     *
     * @param selected the event for which the refund is requested (must not be null)
     * @param row      seat row index
     * @param seat     seat index in the row
     * @return a {@link Ticket} representing the refunded ticket (implementation may return details about the refund)
     * @throws NotBookedException if the seat was not booked
     * @throws IllegalArgumentException if {@code selected} is null or seat indices are invalid
     */
    Ticket refund(CinemaEvent selected, int row, int seat) throws NotBookedException;

    /**
     * Prints a formatted snack menu for this cinema.
     *
     * <p>The actual output destination (console, logger, UI component) is implementation-defined.</p>
     */
    void printSnackMenu();

    /**
     * Returns overall statistics about the cinema as a human-readable string.
     *
     * @return multi-line string containing aggregated statistics (may be empty but never null)
     */
    String getAllStatistics();

    /**
     * Returns a per-room statistics table as a string suitable for display.
     *
     * @return formatted statistics table (may be empty but never null)
     */
    String getPerRoomStatisticsTable();

    // new
    /**
     * Returns the available snacks and their associated data.
     *
     * <p>The Map contents are implementation-defined; commonly the key is a snack identifier
     * (e.g. name or id) and the value describes price and/or metadata. Concrete implementations
     * should document the exact key/value types and semantics.</p>
     *
     * @return a map of snacks (never null)
     */
    Map<Object, Object> getSnacks();

    /**
     * Purchases a snack identified by the given key.
     *
     * @param chosen the snack identifier (e.g. name or id) as expected by {@link #getSnacks()}
     */
    void buySnack(String chosen);
}

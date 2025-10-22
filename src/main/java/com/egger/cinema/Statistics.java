package com.egger.cinema;

public record Statistics(int soldTicketAmount, double percentSold, double income, double averageSoldTicket, double maxIncome) {
    @Override
    public String toString() {
        return String.format("""
                Number of purchased tickets: %d
                Percentage: %.2f%%
                Current income: $%.2f
                Average income per sold ticket: $%.2f
                Total potential income for rooms: $%.2f
                
                """,
                soldTicketAmount,
                percentSold,
                income,
                averageSoldTicket,
                maxIncome
                );
    }
}
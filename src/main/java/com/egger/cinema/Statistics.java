package com.egger.cinema;

import java.math.BigDecimal;

public record Statistics(int soldTicketAmount, double percentSold, BigDecimal income, BigDecimal maxIncome) {
    @Override
    public String toString() {
        return String.format("""
                Number of purchased tickets: %d
                Percentage: %.2f%%
                Current income: $%.2f
                Total potential income: $%.2f
                """,
                soldTicketAmount,
                percentSold,
                income,
                maxIncome
                );
    }
}
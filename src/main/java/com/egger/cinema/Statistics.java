package com.egger.cinema;

import java.math.BigDecimal;

public record Statistics(int soldTicketAmount, double percentSold, BigDecimal income, BigDecimal maxIncome) {
}
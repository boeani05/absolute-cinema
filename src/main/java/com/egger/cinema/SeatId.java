package com.egger.cinema;

public record SeatId(int row, int seat) implements Comparable<SeatId> {
    @Override
    public String toString() {
        return "Seat " + seat + " in row " + row;
    }


    @Override
    public int compareTo(SeatId o) {
        if(o == null){
            return -1;
        }

        int result = Integer.compare(row, o.row);

        if(result == 0){
            result = Integer.compare(seat, o.seat);
        }

        return result;
    }
}

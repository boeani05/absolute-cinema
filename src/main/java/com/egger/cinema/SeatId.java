package com.egger.cinema;

public record SeatId(int row, int seat) implements Comparable<SeatId> {
    @Override
    public String toString() {
        return "Seat " + seat + " in row " + row;
    }


    @Override
    public int compareTo(SeatId o) {
        if(o == null){
            throw new NullPointerException("SeatId cannot be null");
        }

        int result = Integer.compare(this.row, o.row);

        if(result == 0){
            result = Integer.compare(this.seat, o.seat);
        }

        return result;
    }
}

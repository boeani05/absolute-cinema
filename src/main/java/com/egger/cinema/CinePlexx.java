package com.egger.cinema;

public class CinePlexx extends AbstractCinema {

    public CinePlexx() {
        super();
    }

    @Override
    public String getName() {
        return "CinePlexx";
    }

    @Override
    protected void loadData() {
        Mockdata.loadData(this);
    }
}

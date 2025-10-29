package com.egger.cinema;

import java.util.Collection;
import java.util.Collections;

public class CinePlexx extends AbstractCinema {

    public CinePlexx() {
        super();
    }

    @Override
    public String getName() {
        return "CinePlexx";
    }

    @Override
    public Collection<CinemaEvent> getAllEvents() {
        return Collections.unmodifiableList(events);
    }

    @Override
    protected void loadData() {
        Mockdata.loadData(this);
    }
}

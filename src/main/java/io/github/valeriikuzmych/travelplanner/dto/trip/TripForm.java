package io.github.valeriikuzmych.travelplanner.dto.trip;


import io.github.valeriikuzmych.travelplanner.entity.Trip;

import java.time.LocalDate;

public class TripForm {

    private Long id;

    private String city;

    private LocalDate startDate;

    private LocalDate endDate;

    public static TripForm fromEntity(Trip t) {

        TripForm f = new TripForm();

        f.setId(t.getId());
        f.setCity(t.getCity());
        f.setStartDate(t.getStartDate());
        f.setEndDate(t.getEndDate());

        return f;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}


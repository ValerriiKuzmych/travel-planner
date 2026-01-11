package io.github.valeriikuzmych.travelplanner.dto.trip;

import io.github.valeriikuzmych.travelplanner.dto.activity.ActivityDTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TripBasicDTO {


    private Long id;

    private String city;

    private LocalDate startDate;

    private LocalDate endDate;

    private List<ActivityDTO> activities = new ArrayList<>();

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

    public List<ActivityDTO> getActivities() {
        return activities;
    }

    public void setActivities(List<ActivityDTO> activities) {
        this.activities = activities;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TripBasicDTO that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(city, that.city) && Objects.equals(startDate, that.startDate) && Objects.equals(endDate, that.endDate) && Objects.equals(activities, that.activities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, city, startDate, endDate, activities);
    }
}

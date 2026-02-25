package io.github.valeriikuzmych.travelplanner.dto.trip;

import io.github.valeriikuzmych.travelplanner.dto.activity.ActivityDTO;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TripDetailsResponse {

    private Long id;
    private String city;
    private LocalDate startDate;
    private LocalDate endDate;

    private Map<LocalDate, List<ActivityDTO>> activitiesByDate = new HashMap<>();


    public TripDetailsResponse() {
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

    public Map<LocalDate, List<ActivityDTO>> getActivitiesByDate() {
        return activitiesByDate;
    }

    public void setActivitiesByDate(Map<LocalDate, List<ActivityDTO>> activitiesByDate) {
        this.activitiesByDate = activitiesByDate;
    }


}

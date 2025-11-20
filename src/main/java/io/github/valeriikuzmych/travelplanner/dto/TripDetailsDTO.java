package io.github.valeriikuzmych.travelplanner.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TripDetailsDTO {

    private Long id;
    private String city;
    private LocalDate startDate;
    private LocalDate endDate;

    private Map<LocalDate, List<ActivityDTO>> activitiesByDate;

    private boolean editable;


    public TripDetailsDTO() {
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

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TripDetailsDTO that)) return false;
        return editable == that.editable && Objects.equals(id, that.id) && Objects.equals(city, that.city) && Objects.equals(startDate, that.startDate) && Objects.equals(endDate, that.endDate) && Objects.equals(activitiesByDate, that.activitiesByDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, city, startDate, endDate, activitiesByDate, editable);
    }
}

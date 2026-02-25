package io.github.valeriikuzmych.travelplanner.dto.trip;

import io.github.valeriikuzmych.travelplanner.dto.activity.ActivityDTO;
import io.github.valeriikuzmych.travelplanner.entity.Activity;
import io.github.valeriikuzmych.travelplanner.entity.Trip;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TripResponse {


    private Long id;

    private String city;

    private LocalDate startDate;

    private LocalDate endDate;

    private List<ActivityDTO> activities = new ArrayList<>();

    public static TripResponse fromEntity(Trip t) {

        TripResponse dto = new TripResponse();

        dto.setId(t.getId());
        dto.setCity(t.getCity());
        dto.setStartDate(t.getStartDate());
        dto.setEndDate(t.getEndDate());
        dto.setActivities(t.getActivities().stream().map(TripResponse::mapActivity).toList());

        return dto;

    }

    private static ActivityDTO mapActivity(Activity activity) {

        ActivityDTO dto = new ActivityDTO();
        dto.setId(activity.getId());
        dto.setName(activity.getName());
        dto.setNote(activity.getNote());
        dto.setDate(activity.getDate());
        dto.setStartTime(activity.getStartTime());
        dto.setEndTime(activity.getEndTime());

        return dto;
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

    public List<ActivityDTO> getActivities() {
        return activities;
    }

    public void setActivities(List<ActivityDTO> activities) {
        this.activities = activities;
    }

}

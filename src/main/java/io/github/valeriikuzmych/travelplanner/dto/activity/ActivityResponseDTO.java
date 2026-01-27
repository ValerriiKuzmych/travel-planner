package io.github.valeriikuzmych.travelplanner.dto.activity;

import io.github.valeriikuzmych.travelplanner.entity.Activity;

import java.time.LocalDate;
import java.time.LocalTime;

public class ActivityResponseDTO {

    private Long id;
    private Long tripId;
    private String name;
    private String note;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    public ActivityResponseDTO() {
    }

    public static ActivityResponseDTO fromEntity(Activity a) {

        ActivityResponseDTO dto = new ActivityResponseDTO();

        dto.setId(a.getId());
        dto.setTripId(a.getTrip().getId());
        dto.setName(a.getName());
        dto.setNote(a.getNote());
        dto.setDate(a.getDate());
        dto.setStartTime(a.getStartTime());
        dto.setEndTime(a.getEndTime());

        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
}

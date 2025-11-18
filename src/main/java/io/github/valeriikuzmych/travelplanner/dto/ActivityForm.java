package io.github.valeriikuzmych.travelplanner.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class ActivityForm {

    private Long id;
    private Long tripId;

    private String name;
    private String type;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    public ActivityForm() {
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ActivityForm that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(tripId, that.tripId) && Objects.equals(name, that.name) && Objects.equals(type, that.type) && Objects.equals(date, that.date) && Objects.equals(startTime, that.startTime) && Objects.equals(endTime, that.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tripId, name, type, date, startTime, endTime);
    }

    @Override
    public String toString() {
        return "ActivityForm{" + "id=" + id + ", tripId=" + tripId + ", name='" + name + '\'' + ", type='" + type + '\'' + ", date=" + date + ", startTime=" + startTime + ", endTime=" + endTime + '}';
    }
}

package io.github.valeriikuzmych.travelplanner.dto.activity;

import io.github.valeriikuzmych.travelplanner.entity.Activity;
import org.antlr.v4.runtime.misc.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class ActivityForm {

    private Long id;

    private Long tripId;

    private String name;
    private String note;

    private LocalDate date;

    private LocalTime startTime;
    private LocalTime endTime;


    public ActivityForm() {
    }

    public static ActivityForm fromEntity(Activity a) {

        ActivityForm f = new ActivityForm();
        f.setId(a.getId());
        f.setTripId(a.getTrip().getId());
        f.setName(a.getName());
        f.setNote(a.getNote());
        f.setDate(a.getDate());
        f.setStartTime(a.getStartTime());
        f.setEndTime(a.getEndTime());

        return f;

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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ActivityForm that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(tripId, that.tripId) && Objects.equals(name, that.name) && Objects.equals(note, that.note) && Objects.equals(date, that.date) && Objects.equals(startTime, that.startTime) && Objects.equals(endTime, that.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tripId, name, note, date, startTime, endTime);
    }

    @Override
    public String toString() {
        return "ActivityForm{" +
                "id=" + id +
                ", tripId=" + tripId +
                ", name='" + name + '\'' +
                ", note='" + note + '\'' +
                ", date=" + date +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}

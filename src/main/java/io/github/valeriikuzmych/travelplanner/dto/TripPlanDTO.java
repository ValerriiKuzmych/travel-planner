package io.github.valeriikuzmych.travelplanner.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TripPlanDTO {

    private Long tripId;
    private String city;
    private LocalDate startDate;
    private LocalDate endDate;

    Map<LocalDate, WeatherDayDTO> weather;
    Map<LocalDate, List<ActivityDTO>> activities;

    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
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

    public Map<LocalDate, WeatherDayDTO> getWeather() {
        return weather;
    }

    public void setWeather(Map<LocalDate, WeatherDayDTO> weather) {
        this.weather = weather;
    }

    public Map<LocalDate, List<ActivityDTO>> getActivities() {
        return activities;
    }

    public void setActivities(Map<LocalDate, List<ActivityDTO>> activities) {
        this.activities = activities;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TripPlanDTO that)) return false;
        return Objects.equals(tripId, that.tripId) && Objects.equals(city, that.city) && Objects.equals(startDate, that.startDate) && Objects.equals(endDate, that.endDate) && Objects.equals(weather, that.weather) && Objects.equals(activities, that.activities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tripId, city, startDate, endDate, weather, activities);
    }
}

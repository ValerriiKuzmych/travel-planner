package io.github.valeriikuzmych.travelplanner.dto;

import io.github.valeriikuzmych.travelplanner.dto.activity.ActivityDTO;
import io.github.valeriikuzmych.travelplanner.dto.weather.WeatherDayDTO;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TripPlanDTO {

    private Long tripId;

    private String city;

    private LocalDate startDate;
    private LocalDate endDate;

    private List<LocalDate> tripDates;

    private boolean weatherLimited;

    Map<LocalDate, WeatherDayDTO> weather = new HashMap<>();

    Map<LocalDate, List<ActivityDTO>> activities = new HashMap<>();

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

    public boolean isWeatherLimited() {
        return weatherLimited;
    }

    public void setWeatherLimited(boolean weatherLimited) {
        this.weatherLimited = weatherLimited;
    }

    public List<LocalDate> getTripDates() {
        return tripDates;
    }

    public void setTripDates(List<LocalDate> tripDates) {
        this.tripDates = tripDates;
    }

    public List<LocalDate> getWeatherDatesSorted() {
        return weather.keySet().stream()
                .sorted()
                .toList();
    }

    public LocalDate getLastWeatherDate() {
        if (weather == null || weather.isEmpty()) return null;
        return weather.keySet().stream()
                .max(LocalDate::compareTo)
                .orElse(null);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TripPlanDTO dto)) return false;
        return weatherLimited == dto.weatherLimited && Objects.equals(tripId, dto.tripId) && Objects.equals(city, dto.city) && Objects.equals(startDate, dto.startDate) && Objects.equals(endDate, dto.endDate) && Objects.equals(tripDates, dto.tripDates) && Objects.equals(weather, dto.weather) && Objects.equals(activities, dto.activities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tripId, city, startDate, endDate, tripDates, weatherLimited, weather, activities);
    }
}

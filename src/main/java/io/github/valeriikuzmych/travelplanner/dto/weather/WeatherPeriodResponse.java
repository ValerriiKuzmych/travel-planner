package io.github.valeriikuzmych.travelplanner.dto.weather;

public class WeatherPeriodResponse {

    private DayPeriod period;
    private Double temperature;
    private String description;

    public WeatherPeriodResponse(DayPeriod period, Double temperature, String description) {
        this.period = period;
        this.temperature = temperature;
        this.description = description;
    }

    public DayPeriod getPeriod() {
        return period;
    }

    public Double getTemperature() {
        return temperature;
    }

    public String getDescription() {
        return description;
    }
}


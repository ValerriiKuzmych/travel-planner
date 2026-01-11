package io.github.valeriikuzmych.travelplanner.dto.weather;

public class WeatherPeriodDTO {

    private DayPeriod period;
    private Double temperature;
    private String description;

    public WeatherPeriodDTO(DayPeriod period, Double temperature, String description) {
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


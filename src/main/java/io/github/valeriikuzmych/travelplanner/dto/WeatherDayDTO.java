package io.github.valeriikuzmych.travelplanner.dto;

public class WeatherDayDTO {

    private Double temperature;
    private String description;

    public WeatherDayDTO() {
    }

    public WeatherDayDTO(String description, Double temperature) {
        this.description = description;
        this.temperature = temperature;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

package io.github.valeriikuzmych.travelplanner.dto.weather;

public class WeatherTimeResponse {

    private String time;
    private Double temperature;
    private String description;

    public WeatherTimeResponse() {
    }

    public WeatherTimeResponse(String time, Double temperature, String description) {
        this.time = time;
        this.temperature = temperature;
        this.description = description;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

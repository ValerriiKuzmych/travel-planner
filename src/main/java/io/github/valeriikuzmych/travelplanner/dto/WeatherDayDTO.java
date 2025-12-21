package io.github.valeriikuzmych.travelplanner.dto;

import java.util.ArrayList;
import java.util.List;

public class WeatherDayDTO {

    private List<WeatherTimeDTO> times = new ArrayList<>();

    public List<WeatherTimeDTO> getTimes() {
        return times;
    }

    public void setTimes(List<WeatherTimeDTO> times) {
        this.times = times;
    }
}

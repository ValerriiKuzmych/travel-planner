package io.github.valeriikuzmych.travelplanner.dto.weather;

import java.util.ArrayList;
import java.util.List;

public class WeatherDayResponse {

    private List<WeatherPeriodResponse> periods = new ArrayList<>();

    public List<WeatherPeriodResponse> getPeriods() {
        return periods;
    }

    public void setPeriods(List<WeatherPeriodResponse> periods) {
        this.periods = periods;
    }
}

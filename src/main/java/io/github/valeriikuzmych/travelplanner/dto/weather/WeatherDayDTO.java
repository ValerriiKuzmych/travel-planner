package io.github.valeriikuzmych.travelplanner.dto.weather;

import java.util.ArrayList;
import java.util.List;

public class WeatherDayDTO {

    private List<WeatherPeriodDTO> periods = new ArrayList<>();

    public List<WeatherPeriodDTO> getPeriods() {
        return periods;
    }

    public void setPeriods(List<WeatherPeriodDTO> periods) {
        this.periods = periods;
    }
}

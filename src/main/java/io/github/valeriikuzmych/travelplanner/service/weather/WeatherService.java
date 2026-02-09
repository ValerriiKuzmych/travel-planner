package io.github.valeriikuzmych.travelplanner.service.weather;

import java.util.Map;

public interface WeatherService {

    Map<String, Object> getWeather(String city);

}

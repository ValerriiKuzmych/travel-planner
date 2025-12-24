package io.github.valeriikuzmych.travelplanner.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class WeatherService {

    @Value("${weather.api.key}")
    private String apiKey;
    @Value("${weather.forecast.url}")
    private String forecastUrl;
    @Value("${weather.geo.url}")
    private String geoUrl;


    private final RestTemplate restTemplate;

    public WeatherService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Map<String, Object> getWeather(String city) {

        String geoRequest = String.format("%s?q=%s&limit=1&appid=%s", geoUrl, city, apiKey);

        ResponseEntity<List> geoResponse = restTemplate.getForEntity(geoRequest, List.class);

        if (geoResponse.getBody() == null || geoResponse.getBody().isEmpty()) {

            throw new RuntimeException("City not found: " + city);
        }

        Map<String, Object> location = (Map<String, Object>) geoResponse.getBody().get(0);

        Double lat = (Double) location.get("lat");
        Double lon = (Double) location.get("lon");

        String forecastRequest = String.format("%s?lat=%s&lon=%s&appid=%s&units=metric", forecastUrl, lat, lon, apiKey);


        ResponseEntity<Map> forecastResponse = restTemplate.getForEntity(forecastRequest, Map.class);

        if (forecastResponse != null
                && forecastResponse.getStatusCode().is2xxSuccessful()
                && forecastResponse.getBody() != null) {

            return forecastResponse.getBody();
        }

        throw new RuntimeException("Failed to fetch forecast for " + city);
    }

}

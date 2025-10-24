package io.github.valeriikuzmych.travelplanner.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest(properties = {"weather.geo.url=https://api.openweathermap.org/geo/1.0/direct", "weather.forecast.url=https://api.openweathermap.org/data/2.5/forecast", "weather.api.key=test-key"})
class WeatherServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Autowired
    private WeatherService weatherService;

    @BeforeEach
    void setUp() {

        org.springframework.test.util.ReflectionTestUtils.setField(weatherService, "restTemplate", restTemplate);
    }

    @Test
    void getWeather_success() {
        List<Map<String, Object>> geoResponse = List.of(Map.of("lat", 41.8919, "lon", 12.5113));
        when(restTemplate.getForEntity(anyString(), eq(List.class))).thenReturn(ResponseEntity.ok(geoResponse));

        Map<String, Object> forecastResponse = Map.of("city", Map.of("name", "Rome"), "list", List.of(Map.of("dt_txt", "2025-10-14 12:00:00", "main", Map.of("temp", 22.3))));
        when(restTemplate.getForEntity(anyString(), eq(Map.class))).thenReturn(ResponseEntity.ok(forecastResponse));

        Map<String, Object> result = weatherService.getWeather("Rome");

        assertNotNull(result);
        assertEquals("Rome", ((Map<?, ?>) result.get("city")).get("name"));
        assertFalse(((List<?>) result.get("list")).isEmpty());

        verify(restTemplate, times(1)).getForEntity(anyString(), eq(List.class));
        verify(restTemplate, times(1)).getForEntity(anyString(), eq(Map.class));
    }
}
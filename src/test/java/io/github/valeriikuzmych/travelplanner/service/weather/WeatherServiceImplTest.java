package io.github.valeriikuzmych.travelplanner.service.weather;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeatherServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private WeatherServiceImpl weatherService;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(weatherService, "geoUrl",
                "https://api.openweathermap.org/geo/1.0/direct");
        ReflectionTestUtils.setField(weatherService, "forecastUrl",
                "https://api.openweathermap.org/data/2.5/forecast");
        ReflectionTestUtils.setField(weatherService, "apiKey", "test-key");
    }

    @Test
    void getWeather_success() {

        List<Map<String, Object>> geoResponse =
                List.of(Map.of("lat", 41.8919, "lon", 12.5113));

        when(restTemplate.getForEntity(
                contains("/geo/1.0/direct"), eq(List.class)))
                .thenReturn(ResponseEntity.ok(geoResponse));

        Map<String, Object> forecastBody = Map.of(
                "list", List.of(
                        Map.of(
                                "dt", 123456789L,
                                "main", Map.of("temp", 22.3),
                                "weather", List.of(Map.of("description", "clear sky"))
                        )
                )
        );

        when(restTemplate.getForEntity(
                contains("/forecast"), eq(Map.class)))
                .thenReturn(ResponseEntity.ok(forecastBody));

        Map<String, Object> result = weatherService.getWeather("Rome");

        assertNotNull(result);
        assertTrue(result.containsKey("list"));

        verify(restTemplate, times(1))
                .getForEntity(contains("/geo/1.0/direct"), eq(List.class));

        verify(restTemplate, times(1))
                .getForEntity(contains("/forecast"), eq(Map.class));
    }
}


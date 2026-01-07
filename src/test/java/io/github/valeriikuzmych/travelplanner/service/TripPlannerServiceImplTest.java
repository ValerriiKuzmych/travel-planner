package io.github.valeriikuzmych.travelplanner.service;

import io.github.valeriikuzmych.travelplanner.dto.ActivityDTO;
import io.github.valeriikuzmych.travelplanner.dto.TripPlanDTO;
import io.github.valeriikuzmych.travelplanner.dto.WeatherDayDTO;
import io.github.valeriikuzmych.travelplanner.dto.WeatherTimeDTO;
import io.github.valeriikuzmych.travelplanner.entity.Activity;
import io.github.valeriikuzmych.travelplanner.entity.Trip;
import io.github.valeriikuzmych.travelplanner.repository.TripRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TripPlannerServiceImplTest {

    @Mock
    private TripRepository tripRepository;

    @Mock
    private WeatherService weatherService;

    @Mock
    private OwnershipValidator validator;

    @InjectMocks
    private TripPlannerServiceImpl tripPlannerServiceImpl;

    private Trip trip;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        trip = new Trip();
        trip.setId(1L);
        trip.setCity("Rome");
        trip.setStartDate(LocalDate.of(2025, 10, 10));
        trip.setEndDate(LocalDate.of(2025, 10, 15));

        Activity act = new Activity();
        act.setId(5L);
        act.setName("Colosseum Tour");
        act.setDate(LocalDate.of(2025, 10, 12));
        trip.setActivities(List.of(act));
    }

    @Test
    void getPlanForTrip_success() {

        LocalDate today = LocalDate.now();

        trip.setStartDate(today);
        trip.setEndDate(today.plusDays(1));

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        doNothing().when(validator).assertUserOwnTrip(1L, "mail@test.com");

        Instant forecastInstant = Instant.now().plus(1, ChronoUnit.HOURS);
        long dtUtc = forecastInstant.getEpochSecond();

        Map<String, Object> weatherRaw = Map.of(
                "city", Map.of("timezone", 0),
                "list", List.of(
                        Map.of(
                                "dt", dtUtc,
                                "main", Map.of("temp", 20.5),
                                "weather", List.of(
                                        Map.of("description", "clear sky")
                                )
                        )
                )
        );

        when(weatherService.getWeather("Rome")).thenReturn(weatherRaw);

        TripPlanDTO dto = tripPlannerServiceImpl.getPlanForTrip(1L, "mail@test.com");

        assertEquals("Rome", dto.getCity());
        assertTrue(dto.getWeather().containsKey(today));

        WeatherDayDTO day = dto.getWeather().get(today);
        assertFalse(day.getTimes().isEmpty());

        WeatherTimeDTO time = day.getTimes().get(0);


        LocalTime expectedTime =
                Instant.ofEpochSecond(dtUtc)
                        .atZone(ZoneOffset.UTC)
                        .toLocalTime();


        assertEquals(expectedTime.toString(), time.getTime());
        assertEquals(20.5, time.getTemperature());
        assertEquals("clear sky", time.getDescription());
    }

    @Test
    void getPlanForTrip_tripNotFound() {
        when(tripRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> tripPlannerServiceImpl.getPlanForTrip(99L, "user@a.com"));
    }

    @Test
    void getPlanForTrip_weatherLimited_whenForecastDoesNotCoverWholeTrip() {

        LocalDate today = LocalDate.now(ZoneOffset.UTC);

        trip.setStartDate(today.minusDays(1));
        trip.setEndDate(today.plusDays(5));

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        doNothing().when(validator).assertUserOwnTrip(1L, "mail@test.com");


        List<Map<String, Object>> forecastList = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            Instant instant = today.plusDays(i).atStartOfDay().toInstant(ZoneOffset.UTC);

            forecastList.add(Map.of(
                    "dt", instant.getEpochSecond(),
                    "main", Map.of("temp", 18.0 + i),
                    "weather", List.of(Map.of("description", "cloudy"))
            ));
        }

        Map<String, Object> weatherRaw = Map.of(
                "city", Map.of("timezone", 0),
                "list", forecastList
        );

        when(weatherService.getWeather("Rome")).thenReturn(weatherRaw);

        TripPlanDTO dto =
                tripPlannerServiceImpl.getPlanForTrip(1L, "mail@test.com");

        assertFalse(dto.getWeather().isEmpty());
        assertTrue(dto.isWeatherLimited(), "Forecast should be marked as limited");


        LocalDate lastWeatherDate =
                dto.getWeather().keySet().stream().max(LocalDate::compareTo).orElseThrow();

        assertTrue(lastWeatherDate.isBefore(trip.getEndDate()));
    }

    @Test
    void getPlanForTrip_weatherNotLimited_whenForecastCoversWholeTrip() {

        LocalDate today = LocalDate.now(ZoneOffset.UTC);

        trip.setStartDate(today);
        trip.setEndDate(today.plusDays(2));

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        doNothing().when(validator).assertUserOwnTrip(1L, "mail@test.com");

        List<Map<String, Object>> forecastList = new ArrayList<>();

        for (int i = 0; i <= 2; i++) {
            Instant instant = today.plusDays(i).atStartOfDay().toInstant(ZoneOffset.UTC);

            forecastList.add(Map.of(
                    "dt", instant.getEpochSecond(),
                    "main", Map.of("temp", 22.0),
                    "weather", List.of(Map.of("description", "sunny"))
            ));
        }

        Map<String, Object> weatherRaw = Map.of(
                "city", Map.of("timezone", 0),
                "list", forecastList
        );

        when(weatherService.getWeather("Rome")).thenReturn(weatherRaw);

        TripPlanDTO dto =
                tripPlannerServiceImpl.getPlanForTrip(1L, "mail@test.com");

        assertFalse(dto.getWeather().isEmpty());
        assertFalse(dto.isWeatherLimited(), "Forecast fully covers the trip");
    }

    @Test
    void getPlanForTrip_weatherEmpty_whenForecastIsInPast() {

        LocalDate today = LocalDate.now(ZoneOffset.UTC);

        trip.setStartDate(today.plusDays(1));
        trip.setEndDate(today.plusDays(3));

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        doNothing().when(validator).assertUserOwnTrip(1L, "mail@test.com");

        Instant pastInstant =
                today.minusDays(2).atStartOfDay().toInstant(ZoneOffset.UTC);

        Map<String, Object> weatherRaw = Map.of(
                "city", Map.of("timezone", 0),
                "list", List.of(
                        Map.of(
                                "dt", pastInstant.getEpochSecond(),
                                "main", Map.of("temp", 10.0),
                                "weather", List.of(Map.of("description", "rain"))
                        )
                )
        );

        when(weatherService.getWeather("Rome")).thenReturn(weatherRaw);

        TripPlanDTO dto =
                tripPlannerServiceImpl.getPlanForTrip(1L, "mail@test.com");

        assertTrue(dto.getWeather().isEmpty());
        assertTrue(dto.isWeatherLimited());
    }
}

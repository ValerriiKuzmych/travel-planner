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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
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

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        doNothing().when(validator).assertUserOwnTrip(1L, "mail@test.com");


        long dtUtc = LocalDateTime.of(2025, 10, 12, 13, 0)
                .toEpochSecond(ZoneOffset.UTC);

        Map<String, Object> weatherRaw = Map.of(
                "city", Map.of(
                        "timezone", 0
                ),
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
        assertTrue(dto.getWeather().containsKey(LocalDate.of(2025, 10, 12)));

        WeatherDayDTO day =
                dto.getWeather().get(LocalDate.of(2025, 10, 12));

        assertFalse(day.getTimes().isEmpty());

        WeatherTimeDTO time = day.getTimes().get(0);

        assertEquals("13:00", time.getTime());
        assertEquals(20.5, time.getTemperature());
        assertEquals("clear sky", time.getDescription());
    }


    @Test
    void getPlanForTrip_tripNotFound() {
        when(tripRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> tripPlannerServiceImpl.getPlanForTrip(99L, "user@a.com"));
    }
}

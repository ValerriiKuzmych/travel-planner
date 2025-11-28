package io.github.valeriikuzmych.travelplanner.service;

import io.github.valeriikuzmych.travelplanner.dto.ActivityDTO;
import io.github.valeriikuzmych.travelplanner.dto.TripPlanDTO;
import io.github.valeriikuzmych.travelplanner.entity.Activity;
import io.github.valeriikuzmych.travelplanner.entity.Trip;
import io.github.valeriikuzmych.travelplanner.repository.TripRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalTime;
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

        Map<String, Object> weatherRaw = Map.of(
                "list", List.of(
                        Map.of(
                                "dt_txt", "2025-10-12 12:00:00",
                                "main", Map.of("temp", 20.5),
                                "weather", List.of(Map.of("description", "clear sky"))
                        )
                )
        );

        when(weatherService.getWeather("Rome")).thenReturn(weatherRaw);

        TripPlanDTO dto = tripPlannerServiceImpl.getPlanForTrip(1L, "mail@test.com");

        assertEquals("Rome", dto.getCity());
        assertTrue(dto.getWeather().containsKey(LocalDate.of(2025, 10, 12)));
    }

    @Test
    void getPlanForTrip_tripNotFound() {
        when(tripRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> tripPlannerServiceImpl.getPlanForTrip(99L, "user@a.com"));
    }
}

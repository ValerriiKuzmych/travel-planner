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

    @InjectMocks
    private TripPlannerServiceImpl tripPlannerServiceImpl;

    private Trip trip;
    private Activity activity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        trip = new Trip();
        trip.setId(1L);
        trip.setCity("Rome");
        trip.setStartDate(LocalDate.of(2025, 10, 10));
        trip.setEndDate(LocalDate.of(2025, 10, 15));

        activity = new Activity();
        activity.setId(5L);
        activity.setName("Colosseum Tour");
        activity.setDate(LocalDate.of(2025, 10, 12));
        activity.setStartTime(LocalTime.of(10, 0));

        trip.setActivities(List.of(activity));
    }

    @Test
    void getPlanForTrip_success() {

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));

        Map<String, Object> rawWeather = Map.of(
                "list", List.of(
                        Map.of(
                                "dt_txt", "2025-10-12 12:00:00",
                                "main", Map.of("temp", 20.5),
                                "weather", List.of(Map.of("description", "clear sky"))
                        )
                )
        );
        when(weatherService.getWeather("Rome")).thenReturn(rawWeather);

        TripPlanDTO dto = tripPlannerServiceImpl.getPlanForTrip(1L);

        assertEquals(1L, dto.getTripId());
        assertEquals("Rome", dto.getCity());
        assertEquals(LocalDate.of(2025, 10, 10), dto.getStartDate());
        assertEquals(LocalDate.of(2025, 10, 15), dto.getEndDate());

        assertTrue(dto.getActivities().containsKey(activity.getDate()));
        List<ActivityDTO> actList = dto.getActivities().get(activity.getDate());
        assertEquals(1, actList.size());
        assertEquals("Colosseum Tour", actList.get(0).getName());

        assertTrue(dto.getWeather().containsKey(LocalDate.of(2025, 10, 12)));
        assertEquals(20.5, dto.getWeather().get(LocalDate.of(2025, 10, 12)).getTemperature());
        assertEquals("clear sky", dto.getWeather().get(LocalDate.of(2025, 10, 12)).getDescription());

        verify(tripRepository, times(1)).findById(1L);
        verify(weatherService, times(1)).getWeather("Rome");
    }


    @Test
    void getPlanForTrip_tripNotFound() {
        when(tripRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> tripPlannerServiceImpl.getPlanForTrip(99L));
    }
}


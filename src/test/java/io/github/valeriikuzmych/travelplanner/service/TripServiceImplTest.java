package io.github.valeriikuzmych.travelplanner.service;

import io.github.valeriikuzmych.travelplanner.entity.Trip;
import io.github.valeriikuzmych.travelplanner.repository.TripRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class TripServiceImplTest {

    @Mock
    private TripRepository tripRepository;

    @InjectMocks
    private TripServiceImpl tripServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTrip_success() {


        Trip trip = new Trip();

        trip.setCity("Rome");
        trip.setStartDate(LocalDate.of(2026, 10, 10));
        trip.setEndDate(LocalDate.of(2026, 10, 15));

        tripServiceImpl.createTrip(trip);

        verify(tripRepository, times(1)).save(trip);


    }

    @Test
    void createTrip_invalidDates_throwsException() {

        Trip trip = new Trip();
        trip.setCity("Rome");
        trip.setStartDate(LocalDate.of(2025, 10, 20));
        trip.setEndDate(LocalDate.of(2025, 10, 10));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class, () -> tripServiceImpl.createTrip(trip)
        );

        assertEquals("Start time cannot be after end time", ex.getMessage());

        verify(tripRepository, never()).save(any());
    }

    @Test
    void updateTrip_valid_update() {

        Trip existingTrip = new Trip();

        existingTrip.setId(1L);
        existingTrip.setStartDate(LocalDate.of(2026, 10, 10));
        existingTrip.setEndDate(LocalDate.of(2026, 10, 15));
        existingTrip.setCity("Rome");

        when(tripRepository.findById(1L)).thenReturn(Optional.of(existingTrip));

        Trip updatedTrip = new Trip();

        updatedTrip.setCity("London");
        updatedTrip.setStartDate(LocalDate.of(2027, 10, 10));
        updatedTrip.setEndDate(LocalDate.of(2027, 10, 15));


        tripServiceImpl.updateTrip(1L, updatedTrip);

        verify(tripRepository, times(1)).save(existingTrip);


    }

    @Test
    void deleteTrip_success() {

        Long tripId = 1L;

        when(tripRepository.existsById(tripId)).thenReturn(true);

        tripServiceImpl.deleteTrip(1L);

        verify(tripRepository, times(1)).deleteById(tripId);

        verify(tripRepository, never()).save(any());


    }

}

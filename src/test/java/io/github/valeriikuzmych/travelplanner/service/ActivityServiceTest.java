package io.github.valeriikuzmych.travelplanner.service;

import io.github.valeriikuzmych.travelplanner.entity.Activity;
import io.github.valeriikuzmych.travelplanner.entity.Trip;
import io.github.valeriikuzmych.travelplanner.repository.ActivityRepository;
import io.github.valeriikuzmych.travelplanner.repository.TripRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ActivityServiceTest {


    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private TripRepository tripRepository;

    @InjectMocks
    private ActivityService activityService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createActivity_success() {


        Trip trip = new Trip();
        trip.setId(1L);
        trip.setCity("Paris");
        trip.setStartDate(LocalDate.of(2026, 12, 10));
        trip.setEndDate(LocalDate.of(2026, 12, 20));

        Activity activity = new Activity();

        activity.setTrip(trip);
        activity.setName("Sauna");
        activity.setType("Relax");
        activity.setDate(LocalDate.of(2026, 12, 15));
        activity.setStartTime(LocalTime.of(18, 0));
        activity.setEndTime(LocalTime.of(19, 0));

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));

        activityService.createActivity(activity);

        verify(activityRepository, times(1)).save(activity);


    }

    @Test
    void createActivity_invalidTimes_throwsException() {


        Trip trip = new Trip();
        trip.setId(1L);
        trip.setCity("Paris");
        trip.setStartDate(LocalDate.of(2026, 12, 10));
        trip.setEndDate(LocalDate.of(2026, 12, 20));

        Activity activity = new Activity();

        activity.setTrip(trip);
        activity.setName("Sauna");
        activity.setType("Relax");
        activity.setDate(LocalDate.of(2026, 12, 15));
        activity.setStartTime(LocalTime.of(19, 0));
        activity.setEndTime(LocalTime.of(18, 0));

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class, () -> activityService.createActivity(activity)
        );

        assertEquals("Start time cannot be after end time", ex.getMessage());

        verify(activityRepository, never()).save(any());
    }

    @Test
    void updateTime_valid_update() {

        Activity existingActivity = new Activity();

        existingActivity.setId(1L);
        existingActivity.setName("Sauna");
        existingActivity.setType("Relax");
        existingActivity.setDate(LocalDate.of(2026, 12, 15));
        existingActivity.setStartTime(LocalTime.of(18, 0));
        existingActivity.setEndTime(LocalTime.of(19, 0));

        when(activityRepository.findById(1L)).thenReturn(Optional.of(existingActivity));

        Activity updatedActivity = new Activity();

        updatedActivity.setName("Sauna1");
        updatedActivity.setType("Relax1");
        updatedActivity.setDate(LocalDate.of(2027, 1, 16));
        updatedActivity.setStartTime(LocalTime.of(20, 0));
        updatedActivity.setEndTime(LocalTime.of(21, 0));


        activityService.updateActivity(1L, updatedActivity);

        verify(activityRepository, times(1)).save(existingActivity);


    }

    @Test
    void deleteActivity_success() {

        Long activityId = 1L;

        when(activityRepository.existsById(activityId)).thenReturn(true);

        activityService.deleteActivity(1L);

        verify(activityRepository, times(1)).deleteById(activityId);

        verify(activityRepository, never()).save(any());


    }
}

package io.github.valeriikuzmych.travelplanner.service;

import io.github.valeriikuzmych.travelplanner.dto.activity.ActivityForm;
import io.github.valeriikuzmych.travelplanner.entity.Activity;
import io.github.valeriikuzmych.travelplanner.entity.Trip;
import io.github.valeriikuzmych.travelplanner.exception.ResourceNotFoundException;
import io.github.valeriikuzmych.travelplanner.repository.ActivityRepository;
import io.github.valeriikuzmych.travelplanner.repository.TripRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActivityServiceImplTest {

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private TripRepository tripRepository;

    @Mock
    private OwnershipValidator ownershipValidator;

    @InjectMocks
    private ActivityServiceImpl service;

    private Trip trip;
    private Activity activity;
    private ActivityForm validForm;

    @BeforeEach
    void init() {
        trip = new Trip();
        trip.setId(1L);
        trip.setStartDate(LocalDate.of(2026, 12, 10));
        trip.setEndDate(LocalDate.of(2026, 12, 20));

        activity = new Activity();
        activity.setId(10L);
        activity.setTrip(trip);
        activity.setName("Sauna");
        activity.setNote("Relax");
        activity.setDate(LocalDate.of(2026, 12, 15));
        activity.setStartTime(LocalTime.of(18, 0));
        activity.setEndTime(LocalTime.of(19, 0));

        validForm = new ActivityForm();
        validForm.setTripId(1L);
        validForm.setName("Sauna");
        validForm.setNote("Relax");
        validForm.setDate(LocalDate.of(2026, 12, 15));
        validForm.setStartTime(LocalTime.of(18, 0));
        validForm.setEndTime(LocalTime.of(19, 0));
    }

    @Test
    void createActivity_success() {

        doNothing().when(ownershipValidator)
                .assertUserOwnTrip(1L, "user@mail.com");

        when(tripRepository.findById(1L))
                .thenReturn(Optional.of(trip));

        when(activityRepository.save(any(Activity.class)))
                .thenAnswer(a -> {
                    Activity saved = a.getArgument(0);
                    saved.setId(100L);
                    return saved;
                });

        Activity result = service.createActivity(validForm, "user@mail.com");

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals("Sauna", result.getName());
    }

    @Test
    void createActivity_invalidTimes_throwsException() {
        ActivityForm form = new ActivityForm();
        form.setTripId(1L);
        form.setName("Sauna");
        form.setNote("Relax");
        form.setDate(LocalDate.of(2026, 12, 15));
        form.setStartTime(LocalTime.of(19, 0));
        form.setEndTime(LocalTime.of(18, 0));

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.createActivity(form, "user@mail.com")
        );

        assertEquals("Start time cannot be after end time", ex.getMessage());
        verify(activityRepository, never()).save(any());
    }

    @Test
    void updateActivity_success() {

        doNothing().when(ownershipValidator)
                .assertUserOwnActivity(10L, "user@mail.com");

        when(activityRepository.findById(10L))
                .thenReturn(Optional.of(activity));

        Activity updated =
                service.updateActivity(10L, validForm, "user@mail.com");

        assertEquals("Sauna", updated.getName());
        assertEquals(LocalTime.of(18, 0), updated.getStartTime());

        verify(activityRepository).save(activity);
    }

    @Test
    void updateActivity_dateOutsideTrip() {

        validForm.setDate(LocalDate.of(2026, 12, 25));

        doNothing().when(ownershipValidator)
                .assertUserOwnActivity(10L, "user@mail.com");

        when(activityRepository.findById(10L))
                .thenReturn(Optional.of(activity));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.updateActivity(10L, validForm, "user@mail.com")
        );

        assertEquals("Activity date must be within trip dates", ex.getMessage());
    }

    @Test
    void deleteActivity_notFound() {

        doNothing().when(ownershipValidator)
                .assertUserOwnActivity(10L, "user@mail.com");

        when(activityRepository.existsById(10L))
                .thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.deleteActivity(10L, "user@mail.com")
        );
    }


}

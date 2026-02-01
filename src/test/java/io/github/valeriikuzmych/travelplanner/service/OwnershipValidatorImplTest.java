package io.github.valeriikuzmych.travelplanner.service;

import io.github.valeriikuzmych.travelplanner.entity.Activity;
import io.github.valeriikuzmych.travelplanner.entity.Trip;
import io.github.valeriikuzmych.travelplanner.entity.User;
import io.github.valeriikuzmych.travelplanner.exception.ResourceNotFoundException;
import io.github.valeriikuzmych.travelplanner.repository.ActivityRepository;
import io.github.valeriikuzmych.travelplanner.repository.TripRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OwnershipValidatorImplTest {

    @Mock
    TripRepository tripRepository;

    @Mock
    ActivityRepository activityRepository;

    @InjectMocks
    OwnershipValidatorImpl validator;

    @Test
    void assertUserOwnTrip_owner_ok() {

        User user = new User();
        user.setEmail("user@mail.com");

        Trip trip = new Trip();
        trip.setUser(user);

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));

        assertDoesNotThrow(() ->
                validator.assertUserOwnTrip(1L, "user@mail.com"));
    }

    @Test
    void assertUserOwnTrip_notOwner_throwsAccessDenied() {

        User user = new User();
        user.setEmail("owner@mail.com");

        Trip trip = new Trip();
        trip.setUser(user);

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));

        AccessDeniedException ex = assertThrows(
                AccessDeniedException.class,
                () -> validator.assertUserOwnTrip(1L, "hacker@mail.com")
        );

        assertEquals("You do not own this trip", ex.getMessage());
    }

    @Test
    void assertUserOwnTrip_tripNotFound() {

        when(tripRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> validator.assertUserOwnTrip(1L, "user@mail.com")
        );

        assertEquals("Trip not found", ex.getMessage());
    }

    @Test
    void assertUserOwnActivity_notOwner_throwsAccessDenied() {

        User owner = new User();
        owner.setEmail("owner@mail.com");

        Trip trip = new Trip();
        trip.setUser(owner);

        Activity activity = new Activity();
        activity.setTrip(trip);

        when(activityRepository.findById(10L)).thenReturn(Optional.of(activity));

        AccessDeniedException ex = assertThrows(
                AccessDeniedException.class,
                () -> validator.assertUserOwnActivity(10L, "other@mail.com")
        );

        assertEquals("You do not own this activity", ex.getMessage());
    }

}

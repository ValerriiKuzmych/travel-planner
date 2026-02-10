package io.github.valeriikuzmych.travelplanner.service.validator;

import io.github.valeriikuzmych.travelplanner.entity.Activity;
import io.github.valeriikuzmych.travelplanner.entity.Trip;
import io.github.valeriikuzmych.travelplanner.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class OwnershipValidatorImplTest {


    @InjectMocks
    OwnershipValidatorImpl validator;

    @Test
    void assertUserOwnTrip_owner_ok() {

        User user = new User();
        user.setEmail("user@mail.com");

        Trip trip = new Trip();
        trip.setUser(user);

        assertDoesNotThrow(() ->
                validator.assertUserOwnTrip(trip, "user@mail.com"));
    }

    @Test
    void assertUserOwnTrip_notOwner_throwsAccessDenied() {

        User user = new User();
        user.setEmail("owner@mail.com");

        Trip trip = new Trip();
        trip.setUser(user);


        AccessDeniedException ex = assertThrows(
                AccessDeniedException.class,
                () -> validator.assertUserOwnTrip(trip, "hacker@mail.com")
        );

        assertEquals("You do not own this trip", ex.getMessage());
    }


    @Test
    void assertUserOwnActivity_notOwner_throwsAccessDenied() {

        User owner = new User();
        owner.setEmail("owner@mail.com");

        Trip trip = new Trip();
        trip.setUser(owner);

        Activity activity = new Activity();
        activity.setTrip(trip);


        AccessDeniedException ex = assertThrows(
                AccessDeniedException.class,
                () -> validator.assertUserOwnActivity(activity, "other@mail.com")
        );

        assertEquals("You do not own this activity", ex.getMessage());
    }

}

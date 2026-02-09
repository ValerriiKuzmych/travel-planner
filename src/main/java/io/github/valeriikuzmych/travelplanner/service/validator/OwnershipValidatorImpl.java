package io.github.valeriikuzmych.travelplanner.service.validator;

import io.github.valeriikuzmych.travelplanner.entity.Activity;
import io.github.valeriikuzmych.travelplanner.entity.Trip;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class OwnershipValidatorImpl implements OwnershipValidator {


    @Override
    public void assertUserOwnTrip(Trip trip, String userEmail) {

        if (!trip.getUser().getEmail().equalsIgnoreCase(userEmail)) {
            throw new AccessDeniedException("You do not own this trip");
        }
    }

    @Override
    public void assertUserOwnActivity(Activity activity, String userEmail) {

        if (!activity.getTrip().getUser().getEmail().equalsIgnoreCase(userEmail)) {
            throw new AccessDeniedException("You do not own this activity");
        }
    }
}

package io.github.valeriikuzmych.travelplanner.service.validator;

import io.github.valeriikuzmych.travelplanner.entity.Activity;
import io.github.valeriikuzmych.travelplanner.entity.Trip;

public interface OwnershipValidator {

    void assertUserOwnTrip(Trip trip, String userEmail);

    void assertUserOwnActivity(Activity activity, String userEmail);
}


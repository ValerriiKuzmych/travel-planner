package io.github.valeriikuzmych.travelplanner.service;

public interface OwnershipValidator {

    void assertUserOwnTrip(Long tripId, String userEmail);

    void assertUserOwnActivity(Long activityId, String userEmail);
}


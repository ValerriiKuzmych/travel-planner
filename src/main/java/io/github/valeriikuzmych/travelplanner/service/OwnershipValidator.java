package io.github.valeriikuzmych.travelplanner.service;

public interface OwnershipValidator {

    void asserUserOwnTrip(Long tripId, String userEmail);

    void asserUserOwnActivity(Long activityId, String userEmail);
}

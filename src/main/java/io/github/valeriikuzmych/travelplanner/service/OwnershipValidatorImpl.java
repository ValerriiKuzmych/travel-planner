package io.github.valeriikuzmych.travelplanner.service;

import io.github.valeriikuzmych.travelplanner.entity.Activity;
import io.github.valeriikuzmych.travelplanner.entity.Trip;
import io.github.valeriikuzmych.travelplanner.repository.ActivityRepository;
import io.github.valeriikuzmych.travelplanner.repository.TripRepository;

public class OwnershipValidatorImpl implements OwnershipValidator {


    private final TripRepository tripRepository;
    private final ActivityRepository activityRepository;

    public OwnershipValidatorImpl(TripRepository tripRepository,
                                  ActivityRepository activityRepository) {

        this.tripRepository = tripRepository;
        this.activityRepository = activityRepository;

    }

    @Override
    public void assertUserOwnTrip(Long tripId, String userEmail) {

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("trip not found"));

        if (!trip.getUser().getEmail().equals(userEmail)) {

            throw new SecurityException("You do not own this trip");

        }

    }

    @Override
    public void assertUserOwnActivity(Long activityId, String userEmail) {

        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new IllegalArgumentException("activity not found"));

        if (!activity.getTrip().getUser().getEmail().equals(userEmail)) {

            throw new SecurityException("You do not own this activity");
        }

    }
}

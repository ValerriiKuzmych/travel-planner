package io.github.valeriikuzmych.travelplanner.service;

import io.github.valeriikuzmych.travelplanner.entity.Activity;
import io.github.valeriikuzmych.travelplanner.entity.Trip;
import io.github.valeriikuzmych.travelplanner.exception.ResourceNotFoundException;
import io.github.valeriikuzmych.travelplanner.repository.ActivityRepository;
import io.github.valeriikuzmych.travelplanner.repository.TripRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
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
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));

        if (!trip.getUser().getEmail().equalsIgnoreCase(userEmail)) {

            throw new AccessDeniedException("You do not own this trip");

        }

    }

    @Override
    public void assertUserOwnActivity(Long activityId, String userEmail) {

        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new IllegalArgumentException("activity not found"));

        if (!activity.getTrip().getUser().getEmail().equalsIgnoreCase(userEmail)) {

            throw new AccessDeniedException("You do not own this activity");
        }

    }
}

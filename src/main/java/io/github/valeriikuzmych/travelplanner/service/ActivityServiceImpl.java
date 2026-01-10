package io.github.valeriikuzmych.travelplanner.service;

import io.github.valeriikuzmych.travelplanner.dto.ActivityForm;
import io.github.valeriikuzmych.travelplanner.entity.Activity;
import io.github.valeriikuzmych.travelplanner.entity.Trip;
import io.github.valeriikuzmych.travelplanner.exception.ResourceNotFoundException;
import io.github.valeriikuzmych.travelplanner.repository.ActivityRepository;
import io.github.valeriikuzmych.travelplanner.repository.TripRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class ActivityServiceImpl implements ActivityService {


    private final ActivityRepository activityRepository;
    private final TripRepository tripRepository;
    private final OwnershipValidator ownershipValidator;

    public ActivityServiceImpl(ActivityRepository activityRepository, TripRepository tripRepository, OwnershipValidator ownershipValidator) {
        this.activityRepository = activityRepository;
        this.tripRepository = tripRepository;
        this.ownershipValidator = ownershipValidator;

    }


    @Override
    public Activity createActivity(ActivityForm form, String email) {


        Trip trip = tripRepository.findById(form.getTripId())

                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));

        ownershipValidator.assertUserOwnTrip(form.getTripId(), email);


        Activity activity = convertActivityFormToActivity(form);

        activityRepository.save(activity);

        return activity;

    }


    @Override
    public Activity getActivityForUser(Long id, String email) {

        ownershipValidator.assertUserOwnActivity(id, email);

        Optional<Activity> optionalActivity = activityRepository.findById(id);

        if (optionalActivity.isEmpty()) {

            throw new IllegalArgumentException("Activity not found");

        }

        Activity activity = optionalActivity.get();

        return activity;
    }


    @Override
    public Activity updateActivity(Long id, ActivityForm form, String email) {

        ownershipValidator.assertUserOwnActivity(id, email);

        Optional<Activity> optionalActivity = activityRepository.findById(id);

        if (optionalActivity.isEmpty()) {

            throw new IllegalArgumentException("Activity not found");

        }

        Activity existing = optionalActivity.get();

        validateDates(form.getDate(), form.getStartTime(), form.getEndTime(), existing.getTrip());

        existing.setName(form.getName());
        existing.setType(form.getType());
        existing.setDate(form.getDate());
        existing.setStartTime(form.getStartTime());
        existing.setEndTime(form.getEndTime());

        activityRepository.save(existing);

        return existing;
    }


    @Override
    public List<Activity> getActivitiesByTripForUser(Long tripId, String email) {

        ownershipValidator.assertUserOwnTrip(tripId, email);

        return activityRepository.findByTripId(tripId);
    }


    @Override
    public void deleteActivity(Long id, String email) {

        ownershipValidator.assertUserOwnActivity(id, email);

        activityRepository.deleteById(id);
    }

    private void validateDates(LocalDate date, LocalTime start, LocalTime end, Trip trip) {

        if (date == null || start == null || end == null) {
            throw new IllegalArgumentException("Date and time must not be empty");
        }

        if (date.isBefore(trip.getStartDate()) || date.isAfter(trip.getEndDate())) {
            throw new IllegalArgumentException("Activity date must be within trip dates");
        }

        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start time cannot be after end time");
        }
    }

    private Activity convertActivityFormToActivity(ActivityForm form) {

        Trip trip = tripRepository.findById(form.getTripId()).orElseThrow(() -> new IllegalArgumentException("Trip not found"));

        validateDates(form.getDate(), form.getStartTime(), form.getEndTime(), trip);

        Activity activity = new Activity();
        activity.setTrip(trip);
        activity.setName(form.getName());
        activity.setType(form.getType());
        activity.setDate(form.getDate());
        activity.setStartTime(form.getStartTime());
        activity.setEndTime(form.getEndTime());

        return activity;
    }
}



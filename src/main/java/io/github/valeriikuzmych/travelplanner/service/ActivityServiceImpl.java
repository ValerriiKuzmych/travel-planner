package io.github.valeriikuzmych.travelplanner.service;

import io.github.valeriikuzmych.travelplanner.dto.ActivityForm;
import io.github.valeriikuzmych.travelplanner.entity.Activity;
import io.github.valeriikuzmych.travelplanner.entity.Trip;
import io.github.valeriikuzmych.travelplanner.repository.ActivityRepository;
import io.github.valeriikuzmych.travelplanner.repository.TripRepository;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class ActivityServiceImpl implements ActivityService {


    private final ActivityRepository activityRepository;
    private final TripRepository tripRepository;
    private final OwnershipValidator ownershipValidator;

    public ActivityServiceImpl(ActivityRepository activityRepository,
                               TripRepository tripRepository,
                               OwnershipValidator ownershipValidator) {
        this.activityRepository = activityRepository;
        this.tripRepository = tripRepository;
        this.ownershipValidator = ownershipValidator;

    }

    @Override
    public void createActivity(Activity activity) {

        validateDates(
                activity.getDate(),
                activity.getStartTime(),
                activity.getEndTime(),
                activity.getTrip());

        activityRepository.save(activity);
    }


    @Override
    public void createActivity(ActivityForm form, String email) {

        ownershipValidator.assertUserOwnTrip(form.getTripId(), email);

        Activity activity = convertActivityFormToActivity(form);

        activityRepository.save(activity);


    }

    @Override
    public Activity getActivity(Long activityId) {

        Optional<Activity> optionalActivity = activityRepository.findById(activityId);

        if (optionalActivity.isEmpty()) {
            throw new IllegalArgumentException("Activity with id " + activityId + "not found");
        }

        return optionalActivity.get();

    }

    @Override
    public Activity getActivityForUser(Long id, String email) {

        ownershipValidator.assertUserOwnActivity(id, email);

        return getActivity(id);
    }

    @Override
    public void updateActivity(Long id, Activity updatedActivity) {

        Activity activity = getActivity(id);

        validateDates(
                updatedActivity.getDate(),
                updatedActivity.getStartTime(),
                updatedActivity.getEndTime(),
                activity.getTrip());

        activity.setName(updatedActivity.getName());
        activity.setType(updatedActivity.getType());
        activity.setDate(updatedActivity.getDate());
        activity.setStartTime(updatedActivity.getStartTime());
        activity.setEndTime(updatedActivity.getEndTime());

        activityRepository.save(activity);

    }

    @Override
    public void updateActivity(Long id, ActivityForm form) {

        Activity activity = getActivity(id);

        Trip trip = activity.getTrip();


        validateDates(
                form.getDate(),
                form.getStartTime(),
                form.getEndTime(),
                activity.getTrip());


        activity.setName(form.getName());
        activity.setType(form.getType());
        activity.setDate(form.getDate());
        activity.setStartTime(form.getStartTime());
        activity.setEndTime(form.getEndTime());

        activityRepository.save(activity);
    }

    @Override
    public void updateActivity(Long id, ActivityForm form, String email) {

        ownershipValidator.assertUserOwnActivity(id, email);

        Activity existing = getActivity(id);

        validateDates(
                form.getDate(),
                form.getStartTime(),
                form.getEndTime(),
                existing.getTrip());

        existing.setName(form.getName());
        existing.setType(form.getType());
        existing.setDate(form.getDate());
        existing.setStartTime(form.getStartTime());
        existing.setEndTime(form.getEndTime());

        activityRepository.save(existing);
    }


    @Override
    public List<Activity> getActivitiesByTripId(Long tripId) {

        return activityRepository.findByTripId(tripId);
    }

    @Override
    public List<Activity> getActivitiesByTripForUser(Long tripId, String email) {

        ownershipValidator.assertUserOwnTrip(tripId, email);

        return getActivitiesByTripId(tripId);
    }

    @Override
    public void deleteActivity(Long id) {

        if (!activityRepository.existsById(id)) {

            throw new IllegalArgumentException("Activity with id " + id + "not found.");
        }

        activityRepository.deleteById(id);
    }

    @Override
    public void deleteActivity(Long id, String email) {

        ownershipValidator.assertUserOwnActivity(id, email);

        deleteActivity(id);

    }

    private void validateDates(
            LocalDate date,
            LocalTime start,
            LocalTime end,
            Trip trip
    ) {

        if (date.isBefore(trip.getStartDate()) || date.isAfter(trip.getEndDate())) {
            throw new IllegalArgumentException("Activity date must be within trip dates");
        }

        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start time cannot be after end time");
        }
    }

    private Activity convertActivityFormToActivity(ActivityForm form) {

        Trip trip = tripRepository.findById(form.getTripId())
                .orElseThrow(() -> new IllegalArgumentException("Trip not found"));

        validateDates(
                form.getDate(),
                form.getStartTime(),
                form.getEndTime(),
                trip);

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



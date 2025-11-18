package io.github.valeriikuzmych.travelplanner.service;

import io.github.valeriikuzmych.travelplanner.dto.ActivityForm;
import io.github.valeriikuzmych.travelplanner.entity.Activity;
import io.github.valeriikuzmych.travelplanner.entity.Trip;
import io.github.valeriikuzmych.travelplanner.repository.ActivityRepository;
import io.github.valeriikuzmych.travelplanner.repository.TripRepository;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
public class ActivityService implements IActivityService {


    private final ActivityRepository activityRepository;
    private final TripRepository tripRepository;

    public ActivityService(ActivityRepository activityRepository,
                           TripRepository tripRepository) {
        this.activityRepository = activityRepository;
        this.tripRepository = tripRepository;
    }

    @Override
    public void createActivity(Activity activity) {

        if (activity.getTrip() == null || activity.getTrip().getId() == null) {
            throw new IllegalArgumentException("Trip must be provided");
        }

        Trip trip = tripRepository.findById(activity.getTrip().getId())
                .orElseThrow(() -> new IllegalArgumentException("Trip not found"));

        if (activity.getDate().isBefore(trip.getStartDate()) ||
                activity.getDate().isAfter(trip.getEndDate())) {
            throw new IllegalArgumentException("Activity date must be within trip dates");
        }

        if (activity.getStartTime().isAfter(activity.getEndTime())) {
            throw new IllegalArgumentException("Start time cannot be after end time");
        }

        activity.setTrip(trip);

        activityRepository.save(activity);
    }

    @Override
    public void createActivity(ActivityForm form) {


        Trip trip = tripRepository.findById(form.getTripId())
                .orElseThrow(() -> new IllegalArgumentException("Trip not found"));


        if (form.getDate().isBefore(trip.getStartDate()) ||
                form.getDate().isAfter(trip.getEndDate())) {
            throw new IllegalArgumentException("Activity date must be within trip dates");
        }


        if (form.getStartTime().isAfter(form.getEndTime())) {
            throw new IllegalArgumentException("Start time cannot be after end time");
        }


        Activity activity = new Activity();

        activity.setTrip(trip);
        activity.setName(form.getName());
        activity.setType(form.getType());
        activity.setDate(form.getDate());
        activity.setStartTime(form.getStartTime());
        activity.setEndTime(form.getEndTime());

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
    public void updateActivity(Long id, Activity updatedActivity) {

        Activity activity = getActivity(id);

        if (updatedActivity.getStartTime().isAfter(updatedActivity.getEndTime())) {

            throw new IllegalArgumentException("Start time cannot be after end time");
        }

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

        
        if (form.getDate().isBefore(trip.getStartDate()) ||
                form.getDate().isAfter(trip.getEndDate())) {
            throw new IllegalArgumentException("Activity date must be within trip dates");
        }

        if (form.getStartTime().isAfter(form.getEndTime())) {
            throw new IllegalArgumentException("Start time cannot be after end time");
        }

        activity.setName(form.getName());
        activity.setType(form.getType());
        activity.setDate(form.getDate());
        activity.setStartTime(form.getStartTime());
        activity.setEndTime(form.getEndTime());

        activityRepository.save(activity);
    }


    @Override
    public List<Activity> getActivitiesByTripId(Long tripId) {

        return activityRepository.findByTripId(tripId);
    }

    @Override
    public void deleteActivity(Long id) {

        if (!activityRepository.existsById(id)) {

            throw new IllegalArgumentException("Activity with id " + id + "not found.");
        }

        activityRepository.deleteById(id);
    }
}



package io.github.valeriikuzmych.travelplanner.service;

import io.github.valeriikuzmych.travelplanner.entity.Activity;
import io.github.valeriikuzmych.travelplanner.repository.ActivityRepository;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
public class ActivityService implements IActivityService {


    private final ActivityRepository activityRepository;

    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @Override
    public void createActivity(Activity activity) {

        if (activity.getId() != null) {
            throw new IllegalArgumentException("New activity must not have an ID");
        }

        if (activity.getStartTime().isAfter(activity.getEndTime())) {
            throw new IllegalArgumentException("Start time cannot be after end time");
        }

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
        activity.setStartTime(updatedActivity.getStartTime());
        activity.setEndTime(updatedActivity.getEndTime());

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



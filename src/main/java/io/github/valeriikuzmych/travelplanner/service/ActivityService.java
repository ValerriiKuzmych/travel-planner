package io.github.valeriikuzmych.travelplanner.service;

import io.github.valeriikuzmych.travelplanner.dto.ActivityForm;
import io.github.valeriikuzmych.travelplanner.entity.Activity;

import java.util.List;


public interface ActivityService {


    Activity createActivity(ActivityForm form, String email);

    Activity getActivityForUser(Long id, String email);

    Activity updateActivity(Long id, ActivityForm form, String email);

    List<Activity> getActivitiesByTripForUser(Long tripId, String email);

    void deleteActivity(Long id, String email);
}

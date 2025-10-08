package io.github.valeriikuzmych.travelplanner.service;

import io.github.valeriikuzmych.travelplanner.entity.Activity;
import org.springframework.stereotype.Service;

import java.util.List;


public interface IActivityService {

    void createActivity(Activity activity);

    void updateActivity(Long id, Activity updatedActivity);

    Activity getActivity(Long id);

    void deleteActivity(Long id);

    List<Activity> getActivitiesByTripId(Long tripId);
}

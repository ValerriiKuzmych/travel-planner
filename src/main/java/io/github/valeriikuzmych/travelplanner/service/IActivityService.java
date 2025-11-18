package io.github.valeriikuzmych.travelplanner.service;

import io.github.valeriikuzmych.travelplanner.dto.ActivityForm;
import io.github.valeriikuzmych.travelplanner.entity.Activity;
import org.springframework.stereotype.Service;

import java.util.List;


public interface IActivityService {


    void createActivity(Activity activity);

    void createActivity(ActivityForm form);

    Activity getActivity(Long id);

    void updateActivity(Long id, Activity updatedActivity);
    
    void updateActivity(Long id, ActivityForm form);

    List<Activity> getActivitiesByTripId(Long tripId);

    void deleteActivity(Long id);
}

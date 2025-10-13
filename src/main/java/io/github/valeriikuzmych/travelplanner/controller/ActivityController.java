package io.github.valeriikuzmych.travelplanner.controller;

import io.github.valeriikuzmych.travelplanner.entity.Activity;
import io.github.valeriikuzmych.travelplanner.service.IActivityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/activities")
public class ActivityController {

    private final IActivityService activityService;


    public ActivityController(IActivityService activityService) {

        this.activityService = activityService;

    }


    @PostMapping
    public ResponseEntity<String> createActivity(@RequestBody Activity activity) {

        activityService.createActivity(activity);

        return ResponseEntity.ok("Activity created successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Activity> getActivity(@PathVariable Long id) {

        try {
            Activity activity = activityService.getActivity(id);

            return ResponseEntity.ok(activity);

        } catch (IllegalArgumentException ex) {

            return ResponseEntity.notFound().build();

        }
    }


    @GetMapping("/trip/{tripId}")
    public ResponseEntity<List<Activity>> getActivities(@PathVariable Long tripId) {

        List<Activity> activities = activityService.getActivitiesByTripId(tripId);

        return ResponseEntity.ok(activities);


    }


    @PutMapping("/{activityId}")
    public ResponseEntity<String> updateActivity(@PathVariable Long activityId, @RequestBody Activity updatedActivity) {

        try {
            activityService.updateActivity(activityId, updatedActivity);

            return ResponseEntity.ok("Activity updated successfully");
        } catch (IllegalArgumentException ex) {

            return ResponseEntity.badRequest().body(ex.getMessage());


        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteActivity(@PathVariable Long id) {


        try {

            activityService.deleteActivity(id);
            return ResponseEntity.ok("Activity deleted successfully");
        } catch (IllegalArgumentException ex) {

            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }


}



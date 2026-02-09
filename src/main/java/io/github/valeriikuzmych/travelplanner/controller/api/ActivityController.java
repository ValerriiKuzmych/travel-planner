package io.github.valeriikuzmych.travelplanner.controller.api;

import io.github.valeriikuzmych.travelplanner.dto.activity.ActivityForm;
import io.github.valeriikuzmych.travelplanner.dto.activity.ActivityResponse;
import io.github.valeriikuzmych.travelplanner.entity.Activity;
import io.github.valeriikuzmych.travelplanner.service.activity.ActivityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {

        this.activityService = activityService;

    }


    @PostMapping
    public ResponseEntity<ActivityResponse> create(@RequestBody ActivityForm form,
                                                   Principal principal) {

        Activity created = activityService.createActivity(form, principal.getName());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ActivityResponse.fromEntity(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActivityResponse> getActivity(@PathVariable Long id,
                                                        Principal principal) {

        Activity activity = activityService.getActivityForUser(id, principal.getName());

        return ResponseEntity.ok(ActivityResponse.fromEntity(activity));
    }


    @GetMapping("/trip/{tripId}")
    public ResponseEntity<List<ActivityResponse>> getByTrip(@PathVariable Long tripId,
                                                            Principal principal) {

        List<Activity> activities =
                activityService.getActivitiesByTripForUser(tripId, principal.getName());

        List<ActivityResponse> dtos = activities.stream()
                .map(ActivityResponse::fromEntity)
                .toList();

        return ResponseEntity.ok(dtos);
    }


    @PutMapping("/{id}")
    public ResponseEntity<ActivityResponse> updateActivity(@PathVariable Long id,
                                                           @RequestBody ActivityForm form,
                                                           Principal principal) {


        Activity updated = activityService.updateActivity(id, form, principal.getName());

        return ResponseEntity.ok(ActivityResponse.fromEntity(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       Principal principal) {

        activityService.deleteActivity(id, principal.getName());

        return ResponseEntity.noContent().build();
    }


}



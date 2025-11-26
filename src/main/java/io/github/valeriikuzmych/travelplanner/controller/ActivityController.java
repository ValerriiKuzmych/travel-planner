package io.github.valeriikuzmych.travelplanner.controller;

import io.github.valeriikuzmych.travelplanner.dto.ActivityForm;
import io.github.valeriikuzmych.travelplanner.entity.Activity;
import io.github.valeriikuzmych.travelplanner.service.ActivityService;
import io.github.valeriikuzmych.travelplanner.service.OwnershipValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {

    private final ActivityService activityService;
    private final OwnershipValidator ownershipValidator;

    public ActivityController(ActivityService activityService,
                              OwnershipValidator ownershipValidator) {
        this.activityService = activityService;
        this.ownershipValidator = ownershipValidator;
    }


    @PostMapping
    public ResponseEntity<Activity> create(@RequestBody ActivityForm form,
                                           Principal principal) {
        String email = principal.getName();

        activityService.createActivity(form, email);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Activity> getActivity(@PathVariable Long id,
                                                Principal principal) {

        String email = principal.getName();

        Activity activity = activityService.getActivityForUser(id, email);

        return ResponseEntity.ok(activity);
    }


    @GetMapping("/trip/{tripId}")
    public ResponseEntity<List<Activity>> getByTrip(@PathVariable Long tripId,
                                                    Principal principal) {

        String email = principal.getName();

        ownershipValidator.assertUserOwnTrip(tripId, email);

        List<Activity> activities =
                activityService.getActivitiesByTripForUser(tripId, email);

        return ResponseEntity.ok(activities);
    }


    @PutMapping("/{activityId}")
    public ResponseEntity<?> updateActivity(@PathVariable Long id,
                                            @RequestBody ActivityForm form,
                                            Principal principal) {

        String email = principal.getName();

        activityService.updateActivity(id, form, email);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id,
                                    Principal principal) {

        String email = principal.getName();

        activityService.deleteActivity(id, email);

        return ResponseEntity.noContent().build();
    }


}



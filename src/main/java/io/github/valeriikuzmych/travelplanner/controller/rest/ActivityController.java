package io.github.valeriikuzmych.travelplanner.controller.rest;

import io.github.valeriikuzmych.travelplanner.dto.activity.ActivityForm;
import io.github.valeriikuzmych.travelplanner.dto.activity.ActivityResponseDTO;
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

    public ActivityController(ActivityService activityService) {

        this.activityService = activityService;

    }


    @PostMapping
    public ResponseEntity<ActivityResponseDTO> create(@RequestBody ActivityForm form,
                                                      Principal principal) {

        Activity created = activityService.createActivity(form, principal.getName());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ActivityResponseDTO.fromEntity(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActivityResponseDTO> getActivity(@PathVariable Long id,
                                                           Principal principal) {

        Activity activity = activityService.getActivityForUser(id, principal.getName());

        return ResponseEntity.ok(ActivityResponseDTO.fromEntity(activity));
    }


    @GetMapping("/trip/{tripId}")
    public ResponseEntity<List<ActivityResponseDTO>> getByTrip(@PathVariable Long tripId,
                                                               Principal principal) {

        List<Activity> activities =
                activityService.getActivitiesByTripForUser(tripId, principal.getName());

        List<ActivityResponseDTO> dtos = activities.stream()
                .map(ActivityResponseDTO::fromEntity)
                .toList();

        return ResponseEntity.ok(dtos);
    }


    @PutMapping("/{id}")
    public ResponseEntity<ActivityResponseDTO> updateActivity(@PathVariable Long id,
                                                              @RequestBody ActivityForm form,
                                                              Principal principal) {


        Activity updated = activityService.updateActivity(id, form, principal.getName());

        return ResponseEntity.ok(ActivityResponseDTO.fromEntity(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       Principal principal) {

        activityService.deleteActivity(id, principal.getName());

        return ResponseEntity.noContent().build();
    }


}



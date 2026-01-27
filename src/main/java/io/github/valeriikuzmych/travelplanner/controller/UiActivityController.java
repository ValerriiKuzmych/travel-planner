package io.github.valeriikuzmych.travelplanner.controller;

import io.github.valeriikuzmych.travelplanner.dto.activity.ActivityForm;
import io.github.valeriikuzmych.travelplanner.entity.Activity;
import io.github.valeriikuzmych.travelplanner.entity.Trip;
import io.github.valeriikuzmych.travelplanner.service.ActivityService;
import io.github.valeriikuzmych.travelplanner.service.OwnershipValidator;
import io.github.valeriikuzmych.travelplanner.service.TripService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("trips/{tripId}/activities")
public class UiActivityController {


    private final ActivityService activityService;
    private final TripService tripService;
    private final OwnershipValidator ownershipValidator;

    public UiActivityController(ActivityService activityService,
                                TripService tripService,
                                OwnershipValidator ownershipValidator) {
        this.activityService = activityService;
        this.tripService = tripService;
        this.ownershipValidator = ownershipValidator;
    }


    @GetMapping
    public String list(@PathVariable Long tripId, Model model, Principal principal) {

        String email = principal.getName();

        Trip trip = tripService.getTrip(tripId, email);

        List<Activity> activityLilst =
                activityService.getActivitiesByTripForUser(tripId, email);

        model.addAttribute("trip", trip);
        model.addAttribute("activities", activityLilst);

        return "activities_list";


    }

    @GetMapping("/create")
    public String createActivityForm(@PathVariable Long tripId,
                                     Model model,
                                     Principal principal) {

        String email = principal.getName();

        ownershipValidator.assertUserOwnTrip(tripId, email);

        ActivityForm form = new ActivityForm();

        form.setTripId(tripId);

        model.addAttribute("form", form);

        return "create_activity";
    }

    @PostMapping("/create")
    public String createActivity(@ModelAttribute("form") ActivityForm form,
                                 Model model, Principal principal) {

        String email = principal.getName();

        try {

            activityService.createActivity(form, email);

            return "redirect:/trips/" + form.getTripId();


        } catch (Exception e) {

            model.addAttribute("error", e.getMessage());

            return "create_activity";
        }
    }

    @GetMapping("/{id}/edit")

    public String editActivityForm(@PathVariable Long tripId,
                                   @PathVariable Long id, Model model,
                                   Principal principal) {

        String email = principal.getName();

        Activity activity = activityService.getActivityForUser(id, email);

        ActivityForm form = ActivityForm.fromEntity(activity);


        model.addAttribute("form", form);

        return "edit_activity";

    }

    @PostMapping("/{id}/edit")
    public String editActivity(@PathVariable Long id,
                               @ModelAttribute("form") ActivityForm form,
                               Model model, Principal principal) {

        String email = principal.getName();

        try {

            activityService.updateActivity(id, form, email);

            return "redirect:/trips/" + form.getTripId();


        } catch (Exception e) {

            model.addAttribute("error", e.getMessage());

            return "edit_activity";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteActivity(@PathVariable Long tripId,
                                 @PathVariable Long id,
                                 Principal principal) {

        String email = principal.getName();

        activityService.deleteActivity(id, email);

        return "redirect:/trips/" + tripId;

    }


}

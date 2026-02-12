package io.github.valeriikuzmych.travelplanner.controller.ui;

import io.github.valeriikuzmych.travelplanner.dto.activity.ActivityForm;
import io.github.valeriikuzmych.travelplanner.entity.Activity;
import io.github.valeriikuzmych.travelplanner.entity.Trip;
import io.github.valeriikuzmych.travelplanner.service.activity.ActivityService;
import io.github.valeriikuzmych.travelplanner.service.trip.TripService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("trips/{tripId}/activities")
public class UiActivityController {


    private final ActivityService activityService;
    private final TripService tripService;

    public UiActivityController(ActivityService activityService,
                                TripService tripService) {

        this.activityService = activityService;
        this.tripService = tripService;
    }


    @GetMapping
    public String list(@PathVariable Long tripId, Model model, Principal principal) {

        String email = principal.getName();

        Trip trip = tripService.getTrip(tripId, email);


        model.addAttribute("trip", trip);
        model.addAttribute("activities",
                activityService.getActivitiesByTripForUser(tripId, email));

        return "activities_list";


    }

    @GetMapping("/create")
    public String createActivityForm(@PathVariable Long tripId,
                                     Model model,
                                     Principal principal) {


        tripService.getTrip(tripId, principal.getName());

        ActivityForm form = new ActivityForm();
        form.setTripId(tripId);

        model.addAttribute("form", form);

        return "create_activity";
    }

    @PostMapping("/create")
    public String createActivity(@ModelAttribute("form") ActivityForm form,
                                 Model model, Principal principal) {

        try {

            activityService.createActivity(form, principal.getName());

            return "redirect:/trips/" + form.getTripId();

        } catch (IllegalArgumentException ex) {

            model.addAttribute("form", form);
            model.addAttribute("error", ex.getMessage());

            return "create_activity";
        }
    }

    @GetMapping("/{id}/edit")

    public String editActivityForm(@PathVariable Long tripId,
                                   @PathVariable Long id, Model model,
                                   Principal principal) {


        Activity activity = activityService.getActivityForUser(id, principal.getName());


        model.addAttribute("form", ActivityForm.fromEntity(activity));

        return "edit_activity";

    }

    @PostMapping("/{id}/edit")
    public String editActivity(@PathVariable Long id,
                               @ModelAttribute("form") ActivityForm form,
                               Model model, Principal principal) {

        try {

            activityService.updateActivity(id, form, principal.getName());

            return "redirect:/trips/" + form.getTripId();

        } catch (IllegalArgumentException ex) {

            model.addAttribute("form", form);
            model.addAttribute("error", ex.getMessage());

            return "edit_activity";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteActivity(@PathVariable Long tripId,
                                 @PathVariable Long id,
                                 Principal principal) {

        activityService.deleteActivity(id, principal.getName());

        return "redirect:/trips/" + tripId;

    }


}

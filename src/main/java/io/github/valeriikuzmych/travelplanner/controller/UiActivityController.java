package io.github.valeriikuzmych.travelplanner.controller;

import io.github.valeriikuzmych.travelplanner.dto.ActivityForm;
import io.github.valeriikuzmych.travelplanner.entity.Activity;
import io.github.valeriikuzmych.travelplanner.service.IActivityService;
import io.github.valeriikuzmych.travelplanner.service.ITripService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("trips/{tripId}/activities")
public class UiActivityController {


    private final IActivityService activityService;
    private final ITripService tripService;

    public UiActivityController(IActivityService activityService, ITripService tripService) {

        this.activityService = activityService;
        this.tripService = tripService;

    }


    @GetMapping
    public String list(@PathVariable Long tripId, Model model) {

        model.addAttribute("trip", tripService.getTrip(tripId));
        model.addAttribute("activities", activityService.getActivitiesByTripId(tripId));

        return "activities_list";


    }

    @GetMapping("/create")
    public String createActivityForm(@PathVariable Long tripId, Model model) {

        ActivityForm form = new ActivityForm();
        form.setTripId(tripId);

        model.addAttribute("form", form);

        return "create_activity";
    }

    @PostMapping("/create")
    public String createActivity(@ModelAttribute("form") ActivityForm form, Model model) {

        try {

            activityService.createActivity(form);

            return "redirect:/trips/" + form.getTripId() + "/activities";

        } catch (Exception e) {

            model.addAttribute("error", e.getMessage());

            return "activity_create";
        }
    }

    @GetMapping("/{id}/edit")

    public String editActivityForm(@PathVariable Long tripId, @PathVariable Long id, Model model) {

        Activity activity = activityService.getActivity(id);

        ActivityForm form = new ActivityForm();

        form.setId(activity.getId());
        form.setTripId(tripId);
        form.setName(activity.getName());
        form.setType(activity.getType());
        form.setDate(activity.getDate());
        form.setStartTime(activity.getStartTime());
        form.setEndTime(activity.getEndTime());

        model.addAttribute("form", form);

        return "edit_activity";

    }

    @PostMapping("/{id}/edit")
    public String editActivity(@PathVariable Long id, @ModelAttribute("form") ActivityForm form, Model model) {

        try {

            activityService.updateActivity(id, form);

            return "redirect:/trips/" + form.getTripId() + "/activities";

        } catch (Exception e) {

            model.addAttribute("error", e.getMessage());

            return "edit_activity";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteActivity(@PathVariable Long tripId, @PathVariable Long id) {

        activityService.deleteActivity(id);

        return "redirect:/trips/" + tripId + "/activities";
    }


}

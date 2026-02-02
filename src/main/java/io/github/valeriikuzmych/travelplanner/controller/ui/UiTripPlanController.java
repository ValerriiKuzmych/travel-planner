package io.github.valeriikuzmych.travelplanner.controller.ui;


import io.github.valeriikuzmych.travelplanner.dto.TripPlanDTO;
import io.github.valeriikuzmych.travelplanner.service.TripPlannerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/trips")
public class UiTripPlanController {

    private final TripPlannerService tripPlannerService;

    public UiTripPlanController(TripPlannerService tripPlannerService) {

        this.tripPlannerService = tripPlannerService;
    }

    @GetMapping("/{id}/plan")
    public String showTripPlan(@PathVariable Long id, Principal principal,
                               Model model) {

        try {
            TripPlanDTO plan =
                    tripPlannerService.getPlanForTrip(id, principal.getName());

            model.addAttribute("plan", plan);
            return "trip-plan";

        } catch (IllegalArgumentException ex) {
            return "error";

        }


    }
}

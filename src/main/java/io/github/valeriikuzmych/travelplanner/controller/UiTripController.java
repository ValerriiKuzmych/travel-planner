package io.github.valeriikuzmych.travelplanner.controller;


import io.github.valeriikuzmych.travelplanner.entity.Trip;
import io.github.valeriikuzmych.travelplanner.service.ITripService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/trips")
public class UiTripController {

    private final ITripService tripService;

    public UiTripController(ITripService tripService) {
        this.tripService = tripService;
    }

    @GetMapping
    public String tripsPage(Model model, Principal principal) {

        String email = principal.getName();

        List<Trip> trips = tripService.getTripsForUser(email);

        model.addAttribute("trips", trips);

        return "trips";
    }


}

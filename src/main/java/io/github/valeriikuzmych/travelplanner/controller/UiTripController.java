package io.github.valeriikuzmych.travelplanner.controller;


import io.github.valeriikuzmych.travelplanner.entity.Trip;
import io.github.valeriikuzmych.travelplanner.service.ITripService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/create")
    public String createTripForm(Model model) {

        model.addAttribute("trip", new Trip());

        return "create_trip";
    }

    @PostMapping("/create")
    public String createTrip(@ModelAttribute Trip trip, Principal principal) {

        tripService.createTripForUser(principal.getName(), trip);

        return "redirect:/trips";
    }

    @GetMapping("/{id}/edit")
    public String editTripForm(@PathVariable Long id, Model model) {

        Trip trip = tripService.getTrip(id);

        model.addAttribute("trip", trip);

        return "edit_trip";
    }

    @PostMapping("/{id}/edit")
    public String updateTrip(@PathVariable Long id, @ModelAttribute Trip trip) {

        tripService.updateTrip(id, trip);

        return "redirect:/trips";
    }

    @PostMapping("/{id}/delete")
    public String deleteTrip(@PathVariable Long id) {

        tripService.deleteTrip(id);

        return "redirect:/trips";
    }


}



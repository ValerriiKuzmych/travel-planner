package io.github.valeriikuzmych.travelplanner.controller;


import io.github.valeriikuzmych.travelplanner.dto.TripDetailsDTO;
import io.github.valeriikuzmych.travelplanner.entity.Trip;
import io.github.valeriikuzmych.travelplanner.service.TripService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/trips")
public class UiTripController {

    private final TripService tripService;

    public UiTripController(TripService tripService) {
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
    public String createTrip(@ModelAttribute Trip trip,
                             Principal principal) {

        String email = principal.getName();

        tripService.createTripForUser(email, trip);

        return "redirect:/trips";
    }

    @GetMapping("/{id}/edit")
    public String editTripForm(@PathVariable Long id,
                               Model model, Principal principal) {

        String email = principal.getName();

        Trip trip = tripService.getTripForUser(id, email);

        model.addAttribute("trip", trip);

        return "edit_trip";
    }

    @PostMapping("/{id}/edit")
    public String updateTrip(@PathVariable Long id,
                             @ModelAttribute Trip trip,
                             Principal principal) {


        String email = principal.getName();

        tripService.updateTripForUser(id, trip, email);

        return "redirect:/trips";
    }

    @PostMapping("/{id}/delete")
    public String deleteTrip(@PathVariable Long id, Principal principal) {

        String email = principal.getName();

        tripService.deleteTripForUser(id, email);

        return "redirect:/trips";
    }

    @GetMapping("/{id}")
    public String viewTrip(
            @PathVariable Long id,
            Model model,
            Principal principal) {

        TripDetailsDTO dto = tripService.getTripDetailsForUser(id, principal.getName());

        model.addAttribute("trip", dto);

        return "trip_details";
    }


}



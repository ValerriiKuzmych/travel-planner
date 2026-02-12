package io.github.valeriikuzmych.travelplanner.controller.ui;


import io.github.valeriikuzmych.travelplanner.dto.trip.TripResponse;
import io.github.valeriikuzmych.travelplanner.dto.trip.TripDetailsResponse;
import io.github.valeriikuzmych.travelplanner.dto.trip.TripForm;
import io.github.valeriikuzmych.travelplanner.entity.Trip;
import io.github.valeriikuzmych.travelplanner.service.validator.OwnershipValidator;
import io.github.valeriikuzmych.travelplanner.service.trip.TripService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/trips")
public class UiTripController {

    private final TripService tripService;
    private final OwnershipValidator ownershipValidator;

    public UiTripController(TripService tripService,
                            OwnershipValidator ownershipValidator) {

        this.tripService = tripService;
        this.ownershipValidator = ownershipValidator;
    }

    @GetMapping
    public String tripsPage(Model model, Principal principal) {

        String email = principal.getName();

        List<TripResponse> trips = tripService.getTripsForUser(email);

        model.addAttribute("trips", trips);

        return "trips";
    }

    @GetMapping("/create")
    public String createTripForm(Model model) {

        model.addAttribute("trip", new TripForm());
        return "create_trip";
    }

    @PostMapping("/create")
    public String createTrip(@ModelAttribute TripForm form, Model model,
                             Principal principal) {

        try {
            tripService.createTrip(form, principal.getName());

            return "redirect:/trips";

        } catch (IllegalArgumentException ex) {

            model.addAttribute("trip", form);
            model.addAttribute("error", ex.getMessage());

            return "create_trip";
        }
    }


    @GetMapping("/{id}/edit")
    public String editTripForm(@PathVariable Long id,
                               Model model, Principal principal) {

        String email = principal.getName();

        Trip trip = tripService.getTrip(id, email);

        TripForm form = TripForm.fromEntity(trip);

        model.addAttribute("form", form);

        return "edit_trip";
    }

    @PostMapping("/{id}/edit")
    public String updateTrip(@PathVariable Long id,
                             @ModelAttribute TripForm form, Model model,
                             Principal principal) {
        try {

            tripService.updateTrip(id, form, principal.getName());

            return "redirect:/trips/" + id;

        } catch (IllegalArgumentException ex) {

            model.addAttribute("form", form);
            model.addAttribute("error", ex.getMessage());
            
            return "edit_trip";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteTrip(@PathVariable Long id, Principal principal) {

        String email = principal.getName();

        tripService.deleteTrip(id, email);

        return "redirect:/trips";
    }

    @GetMapping("/{id}")
    public String viewTrip(
            @PathVariable Long id, Model model, Principal principal) {

        String email = principal.getName();

        TripDetailsResponse dto = tripService.getTripDetails(id, email);

        model.addAttribute("trip", dto);

        return "trip_details";
    }


}



package io.github.valeriikuzmych.travelplanner.controller.ui;


import io.github.valeriikuzmych.travelplanner.dto.trip.TripBasicDTO;
import io.github.valeriikuzmych.travelplanner.dto.trip.TripDetailsDTO;
import io.github.valeriikuzmych.travelplanner.dto.trip.TripForm;
import io.github.valeriikuzmych.travelplanner.entity.Trip;
import io.github.valeriikuzmych.travelplanner.service.OwnershipValidator;
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
    private final OwnershipValidator ownershipValidator;

    public UiTripController(TripService tripService,
                            OwnershipValidator ownershipValidator) {

        this.tripService = tripService;
        this.ownershipValidator = ownershipValidator;
    }

    @GetMapping
    public String tripsPage(Model model, Principal principal) {

        String email = principal.getName();

        List<TripBasicDTO> trips = tripService.getTripsForUser(email);

        model.addAttribute("trips", trips);

        return "trips";
    }

    @GetMapping("/create")
    public String createTripForm(Model model) {

        model.addAttribute("trip", new TripForm());
        return "create_trip";
    }

    @PostMapping("/create")
    public String createTrip(@ModelAttribute TripForm form,
                             Principal principal) {


        String email = principal.getName();

        tripService.createTrip(form, email);

        return "redirect:/trips";
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
                             @ModelAttribute TripForm form,
                             Principal principal) {

        String email = principal.getName();

        tripService.updateTrip(id, form, email);

        return "redirect:/trips/" + id;
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

        TripDetailsDTO dto = tripService.getTripDetails(id, email);

        model.addAttribute("trip", dto);

        return "trip_details";
    }


}



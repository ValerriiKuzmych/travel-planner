package io.github.valeriikuzmych.travelplanner.controller;

import io.github.valeriikuzmych.travelplanner.dto.trip.TripBasicDTO;
import io.github.valeriikuzmych.travelplanner.dto.trip.TripDetailsDTO;
import io.github.valeriikuzmych.travelplanner.dto.trip.TripForm;
import io.github.valeriikuzmych.travelplanner.entity.Trip;
import io.github.valeriikuzmych.travelplanner.service.TripService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UiTripControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TripService tripService;


    @Test
    void tripsShouldRedirectToLogin() throws Exception {
        mockMvc.perform(get("/trips").with(csrf())
                        .header("Accept", "text/html"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }


    @Test
    void tripsShouldReturnView() throws Exception {

        List<TripBasicDTO> dtoList = List.of(new TripBasicDTO(), new TripBasicDTO());

        when(tripService.getTripsForUser("user@test.com")).thenReturn(dtoList);

        mockMvc.perform(get("/trips")
                        .with(user("user@test.com"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("trips"))
                .andExpect(model().attributeExists("trips"));
    }


    @Test
    void createTripShouldRedirect() throws Exception {

        mockMvc.perform(post("/trips/create")
                        .with(user("user@test.com"))
                        .with(csrf())
                        .param("city", "Paris")
                        .param("startDate", "2025-01-01")
                        .param("endDate", "2025-01-05"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/trips"));

        verify(tripService).createTrip(any(TripForm.class), eq("user@test.com"));
    }


    @Test
    void editTripFormShouldRender() throws Exception {

        Trip trip = new Trip();
        trip.setId(1L);
        trip.setCity("Rome");
        trip.setStartDate(LocalDate.of(2025, 1, 1));
        trip.setEndDate(LocalDate.of(2025, 1, 5));

        when(tripService.getTrip(1L, "user@test.com")).thenReturn(trip);

        mockMvc.perform(get("/trips/1/edit")
                        .with(user("user@test.com"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("edit_trip"))
                .andExpect(model().attributeExists("form"));
    }


    @Test
    void updateTripShouldRedirect() throws Exception {

        mockMvc.perform(post("/trips/1/edit")
                        .with(user("user@test.com"))
                        .with(csrf())
                        .param("city", "London")
                        .param("startDate", "2025-05-01")
                        .param("endDate", "2025-05-10"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/trips/1"));

        verify(tripService).updateTrip(eq(1L), any(TripForm.class), eq("user@test.com"));
    }


    @Test
    void deleteTripShouldRedirect() throws Exception {

        mockMvc.perform(post("/trips/1/delete")
                        .with(user("user@test.com"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/trips"));

        verify(tripService).deleteTrip(1L, "user@test.com");
    }


    @Test
    void tripDetailsPageShouldRender() throws Exception {

        TripDetailsDTO dto = new TripDetailsDTO();
        dto.setId(1L);
        dto.setCity("Rome");
        dto.setStartDate(LocalDate.of(2025, 1, 1));
        dto.setEndDate(LocalDate.of(2025, 1, 5));
        dto.setActivitiesByDate(new HashMap<>());

        when(tripService.getTripDetails(1L, "test@example.com")).thenReturn(dto);

        mockMvc.perform(get("/trips/1")
                        .with(user("test@example.com"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("trip_details"))
                .andExpect(model().attributeExists("trip"));
    }

    @Test
    void createTripFormShouldRender() throws Exception {

        mockMvc.perform(get("/trips/create")
                        .with(user("user@test.com"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("create_trip"))
                .andExpect(model().attributeExists("trip"));
    }
}
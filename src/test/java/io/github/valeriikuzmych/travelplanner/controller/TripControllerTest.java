package io.github.valeriikuzmych.travelplanner.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.valeriikuzmych.travelplanner.dto.TripForm;
import io.github.valeriikuzmych.travelplanner.entity.Trip;
import io.github.valeriikuzmych.travelplanner.entity.User;
import io.github.valeriikuzmych.travelplanner.exception.ResourceNotFoundException;
import io.github.valeriikuzmych.travelplanner.repository.TripRepository;
import io.github.valeriikuzmych.travelplanner.repository.UserRepository;
import io.github.valeriikuzmych.travelplanner.service.OwnershipValidator;
import io.github.valeriikuzmych.travelplanner.service.TripService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TripControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    TripRepository tripRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ObjectMapper mapper;

    User testUser;
    Trip testTrip;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("TripTest@example.com");
        testUser.setPassword("pass");
        testUser.setRole("USER");
        userRepository.save(testUser);

        testTrip = new Trip();
        testTrip.setCity("TripTest");
        testTrip.setStartDate(LocalDate.of(2026, 10, 10));
        testTrip.setEndDate(LocalDate.of(2026, 10, 15));
        testTrip.setUser(testUser);
        tripRepository.save(testTrip);
    }

    @Test
    @WithMockUser(username = "TripTest@example.com")
    void createTrip_success() throws Exception {

        TripForm form = new TripForm();

        form.setCity("CreatedCity");
        form.setStartDate(LocalDate.of(2027, 1, 1));
        form.setEndDate(LocalDate.of(2027, 1, 5));

        mockMvc.perform(post("/api/trips")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(form)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());

        List<Trip> trips = tripRepository.findByUserId(testUser.getId());

        assertThat(trips).hasSize(2);
    }

    @Test
    void getTrips_success() throws Exception {

        Trip another = new Trip();

        another.setCity("City2");
        another.setStartDate(LocalDate.of(2027, 1, 20));
        another.setEndDate(LocalDate.of(2027, 1, 25));
        another.setUser(testUser);
        tripRepository.save(another);


        mockMvc.perform(get("/api/trips/user/{id}", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("TripTest@example.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].city").value(hasItems("TripTest", "City2")));
    }

    @Test
    @WithMockUser(username = "TripTest@example.com")
    void getTripById_success() throws Exception {


        mockMvc.perform(get("/api/trips/{id}", testTrip.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city").value("TripTest"));
    }

    @Test
    @WithMockUser(username = "TripTest@example.com")
    void updateTrip_success() throws Exception {

        TripForm form = new TripForm();

        form.setCity("Updated");
        form.setStartDate(LocalDate.of(2026, 12, 20));
        form.setEndDate(LocalDate.of(2027, 1, 15));

        mockMvc.perform(put("/api/trips/{id}", testTrip.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(form)))
                .andExpect(status().isOk());

        Trip updated = tripRepository.findById(testTrip.getId()).orElseThrow();

        assertThat(updated.getCity()).isEqualTo("Updated");
    }

    @Test
    @WithMockUser(username = "TripTest@example.com")
    void deleteTrip_success() throws Exception {

        mockMvc.perform(delete("/api/trips/{id}", testTrip.getId())
                        .with(csrf()))
                .andExpect(status().isOk());

        assertThat(tripRepository.findById(testTrip.getId())).isEmpty();
    }

    @Test
    @WithMockUser(username = "other@mail.com")
    void getTrip_forbidden() throws Exception {

        mockMvc.perform(get("/api/trips/{id}", testTrip.getId()))

                .andExpect(status().isForbidden());
    }


}
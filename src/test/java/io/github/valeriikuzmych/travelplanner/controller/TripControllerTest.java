package io.github.valeriikuzmych.travelplanner.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.valeriikuzmych.travelplanner.entity.Trip;
import io.github.valeriikuzmych.travelplanner.entity.User;
import io.github.valeriikuzmych.travelplanner.repository.TripRepository;
import io.github.valeriikuzmych.travelplanner.repository.UserRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    private ObjectMapper objectMapper;

    private User testUser;
    private Trip testTrip;

    @BeforeEach
    void setUp() {

        testUser = new User();
        testUser.setEmail("TripTest@example.com");
        testUser.setPassword("pass123");
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
    @WithMockUser(username = "TripTest@example.com", roles = {"USER"})
    void createTrip_success() throws Exception {

        User testCreateTripUser = new User();
        testCreateTripUser.setEmail("CreateTripTest@example.com");
        testCreateTripUser.setPassword("pass12345");
        testCreateTripUser.setRole("USER");

        userRepository.save(testCreateTripUser);


        Trip testCreateTrip = new Trip();

        testCreateTrip.setCity("CreateTripTest");
        testCreateTrip.setStartDate(LocalDate.of(2027, 10, 10));
        testCreateTrip.setEndDate(LocalDate.of(2027, 10, 15));
        testCreateTrip.setUser(testCreateTripUser);

        String tripJson = objectMapper.writeValueAsString(testCreateTrip);


        mockMvc.perform(post("/trips")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tripJson))
                .andExpect(status().isOk());


        var trips = tripRepository.findByUserId(testCreateTripUser.getId());
        assertThat(trips).hasSize(1);

        Trip savedTrip = trips.get(0);

        assertThat(savedTrip.getCity()).isEqualTo("CreateTripTest");
        assertThat(savedTrip.getUser().getId()).isEqualTo(testCreateTripUser.getId());
        assertThat(savedTrip.getStartDate()).isEqualTo(LocalDate.of(2027, 10, 10));
        assertThat(savedTrip.getEndDate()).isEqualTo(LocalDate.of(2027, 10, 15));
        assertThat(savedTrip.getStartDate().isBefore(savedTrip.getEndDate()));


    }

    @Test
    @WithMockUser(username = "TripTest@example.com", roles = {"USER"})
    void getTrips_success() throws Exception {


        Trip testTrip1 = new Trip();
        testTrip1.setCity("TripCityTest1");
        testTrip1.setStartDate(LocalDate.of(2027, 01, 20));
        testTrip1.setEndDate(LocalDate.of(2027, 01, 25));
        testTrip1.setUser(testUser);

        tripRepository.saveAll(List.of(testTrip, testTrip1));


        mockMvc.perform(get("/trips/user/{userId}", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        var trips = tripRepository.findAll();
        assertThat(trips).hasSize(2);

        Trip savedTrip = trips.get(0);

        assertThat(savedTrip.getCity()).isEqualTo("TripTest");
        assertThat(savedTrip.getUser().getId()).isEqualTo(testUser.getId());
        assertThat(savedTrip.getStartDate()).isEqualTo(LocalDate.of(2026, 10, 10));
        assertThat(savedTrip.getEndDate()).isEqualTo(LocalDate.of(2026, 10, 15));

        Trip savedTrip1 = trips.get(1);

        assertThat(savedTrip1.getCity()).isEqualTo("TripCityTest1");
        assertThat(savedTrip1.getUser().getId()).isEqualTo(testUser.getId());
        assertThat(savedTrip1.getStartDate()).isEqualTo(LocalDate.of(2027, 01, 20));
        assertThat(savedTrip1.getEndDate()).isEqualTo(LocalDate.of(2027, 01, 25));


    }

    @Test
    @WithMockUser(username = "TripTest@example.com", roles = {"USER"})
    void getTripById_success() throws Exception {


        mockMvc.perform(get("/trips/{id}", testTrip.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city").value("TripTest"))
                .andExpect(jsonPath("$.user.email").value("TripTest@example.com"));


    }

    @Test
    @WithMockUser(username = "TripTest@example.com", roles = {"USER"})
    void updateTrip_success() throws Exception {


        Trip updatedTrip = new Trip();
        updatedTrip.setCity("TripUpdatedTest");
        updatedTrip.setStartDate(LocalDate.of(2026, 12, 20));
        updatedTrip.setEndDate(LocalDate.of(2027, 01, 15));


        String tripUpdatedJson = objectMapper.writeValueAsString(updatedTrip);


        mockMvc.perform(put("/trips/{id}", testTrip.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tripUpdatedJson))
                .andExpect(status().isOk());

        Trip updatedTripCheck = tripRepository.findById(testTrip.getId()).get();

        assertThat(updatedTripCheck.getCity()).isEqualTo("TripUpdatedTest");
        assertThat(updatedTripCheck.getStartDate()).isEqualTo(LocalDate.of(2026, 12, 20));
        assertThat(updatedTripCheck.getEndDate()).isEqualTo(LocalDate.of(2027, 01, 15));


    }

    @Test
    @WithMockUser(username = "TripTest@example.com", roles = {"USER"})
    void deleteTrip_success() throws Exception {


        mockMvc.perform(delete("/trips/{id}", testTrip.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());


        assertThat(tripRepository.findById(testTrip.getId())).isEmpty();


    }

}

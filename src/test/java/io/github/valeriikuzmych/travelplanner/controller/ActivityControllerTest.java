package io.github.valeriikuzmych.travelplanner.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.valeriikuzmych.travelplanner.dto.ActivityForm;
import io.github.valeriikuzmych.travelplanner.entity.Activity;
import io.github.valeriikuzmych.travelplanner.entity.Trip;
import io.github.valeriikuzmych.travelplanner.entity.User;
import io.github.valeriikuzmych.travelplanner.repository.ActivityRepository;
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
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ActivityControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ActivityRepository activityRepository;
    @Autowired
    TripRepository tripRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private Trip testTrip;
    private Activity testActivity;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("testactivityuser@example.com");
        testUser.setPassword("testuser123");
        testUser.setRole("USER");
        userRepository.save(testUser);

        testTrip = new Trip();
        testTrip.setUser(testUser);
        testTrip.setCity("Rome");
        testTrip.setStartDate(LocalDate.of(2026, 10, 10));
        testTrip.setEndDate(LocalDate.of(2026, 10, 15));
        tripRepository.save(testTrip);

        testActivity = new Activity();
        testActivity.setType("Sport");
        testActivity.setName("Tennis");
        testActivity.setDate(LocalDate.of(2026, 10, 11));
        testActivity.setStartTime(LocalTime.of(14, 0));
        testActivity.setEndTime(LocalTime.of(15, 0));
        testActivity.setTrip(testTrip);
        activityRepository.save(testActivity);
    }

    @Test
    @WithMockUser(username = "testactivityuser@example.com", roles = {"USER"})
    void createActivity_success() throws Exception {
        ActivityForm form = new ActivityForm();
        form.setTripId(testTrip.getId());
        form.setName("Tennis1");
        form.setType("Sport1");
        form.setDate(LocalDate.of(2026, 10, 11));
        form.setStartTime(LocalTime.of(15, 0));
        form.setEndTime(LocalTime.of(16, 0));

        String json = objectMapper.writeValueAsString(form);

        mockMvc.perform(post("/api/activities").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        var activities = activityRepository.findByTripId(testTrip.getId());
        assertThat(activities).hasSize(2);

        var saved = activities.stream()
                .filter(a -> a.getName().equals("Tennis1"))
                .findFirst().orElseThrow();

        assertThat(saved.getType()).isEqualTo("Sport1");
        assertThat(saved.getTrip().getId()).isEqualTo(testTrip.getId());
        assertThat(saved.getDate()).isEqualTo(LocalDate.of(2026, 10, 11));
        assertThat(saved.getStartTime()).isEqualTo(LocalTime.of(15, 0));
        assertThat(saved.getEndTime()).isEqualTo(LocalTime.of(16, 0));
    }

    @Test
    @WithMockUser(username = "testactivityuser@example.com", roles = {"USER"})
    void getActivitiesByTrip_success() throws Exception {
        mockMvc.perform(get("/api/activities/trip/{tripId}", testTrip.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(testActivity.getName()))
                .andExpect(jsonPath("$[0].type").value(testActivity.getType()))
                .andExpect(jsonPath("$[0].tripId").value(testTrip.getId()));
    }

    @Test
    @WithMockUser(username = "testactivityuser@example.com", roles = {"USER"})
    void getActivityById_success() throws Exception {
        mockMvc.perform(get("/api/activities/{id}", testActivity.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Tennis"))
                .andExpect(jsonPath("$.type").value("Sport"))
                .andExpect(jsonPath("$.tripId").value(testTrip.getId()));
    }

    @Test
    @WithMockUser(username = "testactivityuser@example.com", roles = {"USER"})
    void updateActivity_success() throws Exception {
        ActivityForm form = new ActivityForm();
        form.setTripId(testTrip.getId());
        form.setName("Sauna");
        form.setType("Relax");
        form.setDate(LocalDate.of(2026, 10, 13));
        form.setStartTime(LocalTime.of(18, 0));
        form.setEndTime(LocalTime.of(19, 0));

        String json = objectMapper.writeValueAsString(form);

        mockMvc.perform(put("/api/activities/{id}", testActivity.getId()).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        var updated = activityRepository.findById(testActivity.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("Sauna");
        assertThat(updated.getType()).isEqualTo("Relax");
        assertThat(updated.getDate()).isEqualTo(LocalDate.of(2026, 10, 13));
        assertThat(updated.getStartTime()).isEqualTo(LocalTime.of(18, 0));
        assertThat(updated.getEndTime()).isEqualTo(LocalTime.of(19, 0));
    }

    @Test
    @WithMockUser(username = "testactivityuser@example.com", roles = {"USER"})
    void deleteActivity_success() throws Exception {
        mockMvc.perform(delete("/api/activities/{id}", testActivity.getId()).with(csrf()))
                .andExpect(status().isNoContent());

        assertThat(activityRepository.findById(testActivity.getId())).isEmpty();
    }

    @Test
    @WithMockUser(username = "another@example.com", roles = {"USER"})
    void getActivity_forbiddenForAnotherUser() throws Exception {
        mockMvc.perform(get("/api/activities/{id}", testActivity.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "testactivityuser@example.com", roles = {"USER"})
    void createActivity_invalidTime_returnsBadRequest() throws Exception {

        ActivityForm form = new ActivityForm();
        form.setTripId(testTrip.getId());
        form.setName("Bad");
        form.setType("Bad");
        form.setDate(LocalDate.of(2026, 10, 12));
        form.setStartTime(LocalTime.of(18, 0));
        form.setEndTime(LocalTime.of(17, 0)); // invalid

        mockMvc.perform(post("/api/activities").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Start time cannot be after end time"));
    }
    

}
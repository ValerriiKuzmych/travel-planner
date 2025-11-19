package io.github.valeriikuzmych.travelplanner.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
        testActivity.setStartTime(LocalTime.of(14, 00));
        testActivity.setEndTime(LocalTime.of(15, 00));
        testActivity.setTrip(testTrip);
        activityRepository.save(testActivity);


    }


    @Test
    @WithMockUser(username = "testactivityuser@example.com", roles = {"USER"})
    void createActivity_success() throws Exception {


        Activity createTestActivity = new Activity();
        createTestActivity.setType("Sport1");
        createTestActivity.setName("Tennis1");
        createTestActivity.setDate(LocalDate.of(2026, 10, 11));
        createTestActivity.setStartTime(LocalTime.of(15, 00));
        createTestActivity.setEndTime(LocalTime.of(16, 00));
        createTestActivity.setTrip(testTrip);

        String activityJson = objectMapper.writeValueAsString(createTestActivity);

        mockMvc.perform(post("/activities").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(activityJson))
                .andExpect(status().isOk());

        var activities = activityRepository.findByTripId(testTrip.getId());
        assertThat(activities).hasSize(2);

        Activity savedActivity = activities.get(1);

        assertThat(savedActivity.getType()).isEqualTo("Sport1");
        assertThat(savedActivity.getName()).isEqualTo("Tennis1");
        assertThat(savedActivity.getTrip().getUser().getId()).isEqualTo(testUser.getId());
        assertThat(savedActivity.getTrip().getId()).isEqualTo(testTrip.getId());
        assertThat(savedActivity.getDate()).isEqualTo(LocalDate.of(2026, 10, 11));
        assertThat(savedActivity.getStartTime()).isEqualTo(LocalTime.of(15, 00));
        assertThat(savedActivity.getEndTime()).isEqualTo(LocalTime.of(16, 00));
        assertThat(savedActivity.getStartTime().isBefore(savedActivity.getEndTime()));


    }

    @Test
    @WithMockUser(username = "testactivityuser@example.com", roles = {"USER"})
    void getActivities_success() throws Exception {


        Activity testActivity1 = new Activity();
        testActivity1.setType("Sport1");
        testActivity1.setName("Tennis1");
        testActivity1.setDate(LocalDate.of(2026, 10, 11));
        testActivity1.setStartTime(LocalTime.of(15, 00));
        testActivity1.setEndTime(LocalTime.of(16, 00));
        testActivity1.setTrip(testTrip);

        activityRepository.saveAll(List.of(testActivity, testActivity1));


        mockMvc.perform(get("/activities/trip/{tripId}", testTrip.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        var activities = activityRepository.findAll();

        assertThat(activities).hasSize(2);

        Activity savedAcrivity = activities.get(0);
        assertThat(savedAcrivity.getType()).isEqualTo("Sport");
        assertThat(savedAcrivity.getName()).isEqualTo("Tennis");
        assertThat(savedAcrivity.getTrip().getUser().getId()).isEqualTo(testUser.getId());
        assertThat(savedAcrivity.getTrip().getId()).isEqualTo(testTrip.getId());
        assertThat(savedAcrivity.getDate()).isEqualTo(LocalDate.of(2026, 10, 11));
        assertThat(savedAcrivity.getStartTime()).isEqualTo(LocalTime.of(14, 00));
        assertThat(savedAcrivity.getEndTime()).isEqualTo(LocalTime.of(15, 00));
        assertThat(savedAcrivity.getStartTime().isBefore(testActivity.getEndTime()));

        Activity savedActivity1 = activities.get(1);

        assertThat(savedActivity1.getType()).isEqualTo("Sport1");
        assertThat(savedActivity1.getName()).isEqualTo("Tennis1");
        assertThat(savedActivity1.getTrip().getUser().getId()).isEqualTo(testUser.getId());
        assertThat(savedActivity1.getTrip().getId()).isEqualTo(testTrip.getId());
        assertThat(savedActivity1.getDate()).isEqualTo(LocalDate.of(2026, 10, 11));
        assertThat(savedActivity1.getStartTime()).isEqualTo(LocalTime.of(15, 00));
        assertThat(savedActivity1.getEndTime()).isEqualTo(LocalTime.of(16, 00));
        assertThat(savedActivity1.getStartTime().isBefore(testActivity1.getEndTime()));


    }

    @Test
    @WithMockUser(username = "testactivityuser@example.com", roles = {"USER"})
    void getActivityById_success() throws Exception {


        mockMvc.perform(get("/activities/{id}", testActivity.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("Sport"))
                .andExpect(jsonPath("$.name").value("Tennis"))
                .andExpect(jsonPath("$.trip.user.email").value("testactivityuser@example.com"));


    }

    @Test
    @WithMockUser(username = "testactivityuser@example.com", roles = {"USER"})
    void updateActivity_success() throws Exception {


        Activity updatedActivity = new Activity();
        updatedActivity.setName("Sauna");
        updatedActivity.setType("Relax");
        updatedActivity.setDate(LocalDate.of(2026, 10, 17));
        updatedActivity.setStartTime(LocalTime.of(18, 00));
        updatedActivity.setEndTime(LocalTime.of(19, 00));


        String updatedActivityJson = objectMapper.writeValueAsString(updatedActivity);


        mockMvc.perform(put("/activities/{id}", testActivity.getId()).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedActivityJson))
                .andExpect(status().isOk());

        Activity updatedActivityCheck = activityRepository.findById(testActivity.getId()).get();

        assertThat(updatedActivityCheck.getType()).isEqualTo("Relax");
        assertThat(updatedActivityCheck.getName()).isEqualTo("Sauna");
        assertThat(updatedActivityCheck.getTrip().getUser().getId()).isEqualTo(testUser.getId());
        assertThat(updatedActivityCheck.getTrip().getId()).isEqualTo(testTrip.getId());
        assertThat(updatedActivityCheck.getDate()).isEqualTo(LocalDate.of(2026, 10, 17));
        assertThat(updatedActivityCheck.getStartTime()).isEqualTo(LocalTime.of(18, 00));
        assertThat(updatedActivityCheck.getEndTime()).isEqualTo(LocalTime.of(19, 00));
        assertThat(updatedActivityCheck.getStartTime().isBefore(updatedActivityCheck.getEndTime()));

    }

    @Test
    @WithMockUser(username = "testactivityuser@example.com", roles = {"USER"})
    void deleteActivity_success() throws Exception {


        mockMvc.perform(delete("/activities/{id}", testActivity.getId()).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());


        assertThat(activityRepository.findById(testActivity.getId())).isEmpty();


    }


}

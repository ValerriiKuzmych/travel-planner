package io.github.valeriikuzmych.travelplanner.controller.ui;

import io.github.valeriikuzmych.travelplanner.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;


import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UiAuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserService userService;

    @Test
    void testLoginPageLoads() throws Exception {

        mockMvc.perform(get("/login").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void tripShouldRequiredAuth() throws Exception {

        mockMvc.perform(get("/trips").with(csrf())
                        .header("Accept", "text/html"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void registerPageLoads() throws Exception {

        mockMvc.perform(get("/register").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    @Test
    void registerUser_success_redirectsToLogin() throws Exception {

        mockMvc.perform(post("/register").with(csrf())
                        .param("email", "uitest@t.com")
                        .param("password", "pass")
                        .param("confirmPassword", "pass"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered"));
    }

    @Test
    void registerUser_duplicateEmail_returnsRegisterView() throws Exception {

        userService.registerUser("uitest@t.com", "pass");

        mockMvc.perform(post("/register").with(csrf())
                        .param("email", "uitest@t.com")
                        .param("password", "pass")
                        .param("confirmPassword", "pass"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("error"));
    }
}

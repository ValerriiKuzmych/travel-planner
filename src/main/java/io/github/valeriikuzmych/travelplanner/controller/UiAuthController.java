package io.github.valeriikuzmych.travelplanner.controller;

import io.github.valeriikuzmych.travelplanner.dto.RegistrationRequest;
import io.github.valeriikuzmych.travelplanner.service.UserService;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UiAuthController {

    private final UserService userService;

    public UiAuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String authPage(Model model) {

        model.addAttribute("user", new RegistrationRequest());

        return "login";
    }


    @GetMapping("/register")
    public String registerPage(Model model) {

        model.addAttribute("user", new RegistrationRequest());

        return "register";
    }

    @PostMapping("/register")
    public String registerSubmit(@ModelAttribute("user") RegistrationRequest user,
                                 Model model) {

        try {
            userService.registerUser(user);

            return "redirect:/login?registered";

        } catch (IllegalArgumentException e) {

            model.addAttribute("error", e.getMessage());

            return "register";
        }
    }


}

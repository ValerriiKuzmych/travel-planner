package io.github.valeriikuzmych.travelplanner.controller;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class UiLayoutAdvice {

    @ModelAttribute
    public void addLayoutAttributes(HttpServletRequest request, Model model) {

        String uri = request.getRequestURI();


        boolean isTripsRoot = "/trips".equals(uri);


        model.addAttribute("showBackButton", !isTripsRoot);
    }
}


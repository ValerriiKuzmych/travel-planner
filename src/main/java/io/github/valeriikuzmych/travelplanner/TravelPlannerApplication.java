package io.github.valeriikuzmych.travelplanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class TravelPlannerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TravelPlannerApplication.class, args);
    }


}


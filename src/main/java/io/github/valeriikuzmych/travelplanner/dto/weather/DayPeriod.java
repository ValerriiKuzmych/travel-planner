package io.github.valeriikuzmych.travelplanner.dto.weather;

import java.time.LocalTime;

public enum DayPeriod {

    NIGHT("Night"),
    MORNING("Morning"),
    DAY("Day"),
    EVENING("Evening");

    private final String label;

    DayPeriod(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static DayPeriod from(LocalTime time) {
        int hour = time.getHour();

        if (hour >= 6 && hour < 12) return MORNING;
        if (hour >= 12 && hour < 18) return DAY;
        if (hour >= 18 && hour < 24) return EVENING;
        return NIGHT;
    }
}

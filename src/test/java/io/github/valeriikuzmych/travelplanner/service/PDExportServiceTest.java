package io.github.valeriikuzmych.travelplanner.service;

import io.github.valeriikuzmych.travelplanner.dto.activity.ActivityDTO;
import io.github.valeriikuzmych.travelplanner.dto.TripPlanDTO;
import io.github.valeriikuzmych.travelplanner.dto.weather.DayPeriod;
import io.github.valeriikuzmych.travelplanner.dto.weather.WeatherDayDTO;
import io.github.valeriikuzmych.travelplanner.dto.weather.WeatherPeriodDTO;
import io.github.valeriikuzmych.travelplanner.service.pdf.PDExportService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class PDExportServiceTest {

    @Test
    void exportTripPlanToPdf_generatesValidPdf() throws Exception {


        TripPlanDTO dto = new TripPlanDTO();

        dto.setTripId(1L);
        dto.setCity("Rome");
        dto.setStartDate(LocalDate.of(2025, 10, 10));
        dto.setEndDate(LocalDate.of(2025, 10, 15));


        dto.setTripDates(
                LocalDate.of(2025, 10, 10)
                        .datesUntil(LocalDate.of(2025, 10, 16)) // end exclusive
                        .toList()
        );

        WeatherDayDTO weatherDay = new WeatherDayDTO();

        weatherDay.getPeriods().add(
                new WeatherPeriodDTO(
                        DayPeriod.MORNING,
                        16.0,
                        "rain"
                )
        );

        weatherDay.getPeriods().add(
                new WeatherPeriodDTO(
                        DayPeriod.DAY,
                        20.0,
                        "sunny"
                )
        );

        dto.setWeather(
                Map.of(LocalDate.of(2025, 10, 10), weatherDay)
        );

        ActivityDTO a1 = new ActivityDTO();
        a1.setName("Colosseum Tour");
        a1.setStartTime(LocalTime.of(10, 0));
        a1.setDate(LocalDate.of(2025, 10, 10));

        ActivityDTO a2 = new ActivityDTO();
        a2.setName("Vatican Museum");
        a2.setStartTime(LocalTime.of(14, 30));
        a2.setDate(LocalDate.of(2025, 10, 10));

        dto.setActivities(
                Map.of(LocalDate.of(2025, 10, 10), List.of(a1, a2))
        );

        PDExportService service = new PDExportService();

        byte[] pdfBytes = service.exportTripPlanToPdf(dto);

        assertThat(pdfBytes).isNotNull();
        assertThat(pdfBytes.length).isGreaterThan(100);

        try (PDDocument doc = PDDocument.load(pdfBytes)) {

            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(doc);

            assertThat(text).contains("Trip");
            assertThat(text).contains("Rome");
            assertThat(text).contains("10 OCT");
            assertThat(text).contains("15 OCT");
            assertThat(text).contains("Morning");
            assertThat(text).contains("sunny");
            assertThat(text).contains("Colosseum Tour");
            assertThat(text).contains("Vatican Museum");
        }
    }

}


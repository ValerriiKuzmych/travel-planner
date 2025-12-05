package io.github.valeriikuzmych.travelplanner.service;

import io.github.valeriikuzmych.travelplanner.dto.ActivityDTO;
import io.github.valeriikuzmych.travelplanner.dto.TripPlanDTO;
import io.github.valeriikuzmych.travelplanner.dto.WeatherDayDTO;
import io.github.valeriikuzmych.travelplanner.service.pdf.PDExportService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;

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

        dto.setWeather(Map.of(LocalDate.of(2025, 10, 10), new WeatherDayDTO("Sunny", 22.0), LocalDate.of(2025, 10, 11), new WeatherDayDTO("Cloudy", 18.0)));

        ActivityDTO a1 = new ActivityDTO();
        a1.setName("Colosseum Tour");
        a1.setStartTime(LocalTime.of(10, 0));
        a1.setDate(LocalDate.of(2025, 10, 10));

        ActivityDTO a2 = new ActivityDTO();
        a2.setName("Vatican Museum");
        a2.setStartTime(LocalTime.of(14, 30));
        a2.setDate(LocalDate.of(2025, 10, 10));

        dto.setActivities(Map.of(LocalDate.of(2025, 10, 10), List.of(a1, a2)));


        PDExportService service = new PDExportService();

        byte[] pdfBytes = service.exportTripPlanToPdf(dto);

        assertThat(pdfBytes).isNotNull();
        assertThat(pdfBytes.length).isGreaterThan(100);

        try (PDDocument doc = PDDocument.load(pdfBytes)) {

            PDFTextStripper stripper = new PDFTextStripper();

            String text = stripper.getText(doc);

            assertThat(text).contains("Trip Plan: Rome");
            assertThat(text).contains("Weather Forecast");
            assertThat(text).contains("2025-10-10");
            assertThat(text).contains("Sunny");
            assertThat(text).contains("Colosseum Tour");
            assertThat(text).contains("Vatican Museum");
        }
    }
}

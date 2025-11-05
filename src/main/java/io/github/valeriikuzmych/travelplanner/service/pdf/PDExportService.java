package io.github.valeriikuzmych.travelplanner.service.pdf;

import io.github.valeriikuzmych.travelplanner.dto.ActivityDTO;
import io.github.valeriikuzmych.travelplanner.dto.TripPlanDTO;
import io.github.valeriikuzmych.travelplanner.dto.WeatherDayDTO;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class PDExportService implements IPDExportService {


    @Override
    public byte[] exportTripPlanToPdf(TripPlanDTO plan) throws IOException {
        try (PDDocument document = new PDDocument()) {

            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            PdfState state = new PdfState(page, contentStream, 750);

            state.setY(writeTitle(state, plan));

            state.getContentStream().close();

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

                document.save(baos);

                return baos.toByteArray();

            }
        }

    }

    private PdfState checkPage(PDDocument document, PdfState state) throws IOException {

        if (state.getY() < 100) {

            state.getContentStream().close();

            PDPage newPage = new PDPage();
            document.addPage(newPage);

            PDPageContentStream newContentStream = new PDPageContentStream(document, newPage);

            state.setPage(newPage);
            state.setContentStream(newContentStream);
            state.setY(750);

        }

        return state;
    }

    private int writeTitle(PdfState state, TripPlanDTO plan) throws IOException {

        state.getContentStream().beginText();
        state.getContentStream().setFont(PDType1Font.HELVETICA_BOLD, 18);
        state.getContentStream().newLineAtOffset(50, state.getY());
        state.getContentStream().showText("Trip Plan: " + plan.getCity());
        state.getContentStream().endText();

        return state.getY() - 40;

    }

    private int writeWeather(PDDocument document, PdfState state, TripPlanDTO plan) throws IOException {

        state.getContentStream().beginText();
        state.getContentStream().setFont(PDType1Font.HELVETICA_BOLD, 14);
        state.getContentStream().newLineAtOffset(50, state.getY());
        state.getContentStream().showText("Weather Forecast:");
        state.getContentStream().endText();

        state.setY(state.getY() - 25);

        for (Map.Entry<LocalDate, WeatherDayDTO> entry : plan.getWeather().entrySet()) {

            state = checkPage(document, state);

            WeatherDayDTO weather = entry.getValue();

            state.getContentStream().beginText();
            state.getContentStream().setFont(PDType1Font.HELVETICA, 12);
            state.getContentStream().newLineAtOffset(60, state.getY());
            state.getContentStream().showText(entry.getKey() + ": " + weather.getTemperature() + "°C, " + weather.getDescription());
            state.getContentStream().endText();
        }

        return state.getY() - 20;

    }

    private int writeActivity(PDDocument document, PdfState state, TripPlanDTO plan) throws IOException {

        state = checkPage(document, state);

        state.getContentStream().beginText();
        state.getContentStream().setFont(PDType1Font.HELVETICA_BOLD, 14);
        state.getContentStream().newLineAtOffset(50, state.getY());
        state.getContentStream().showText("Activities:");
        state.getContentStream().endText();


        for (Map.Entry<LocalDate, List<ActivityDTO>> entry : plan.getActivities().entrySet()) {

            state = checkPage(document, state);

            state.getContentStream().beginText();
            state.getContentStream().setFont(PDType1Font.HELVETICA_BOLD, 12);
            state.getContentStream().newLineAtOffset(60, state.getY());
            state.getContentStream().showText(entry.getKey().toString() + ":");
            state.getContentStream().endText();

            for (ActivityDTO act : entry.getValue()) {

                state = checkPage(document, state);

                state.getContentStream().beginText();
                state.getContentStream().setFont(PDType1Font.HELVETICA, 12);
                state.getContentStream().newLineAtOffset(80, state.getY());
                state.getContentStream().showText(act.getStartTime() + "-" + act.getEndTime() + " - " + act.getName());
                state.getContentStream().endText();

                state.setY(state.getY() - 15);


            }

            state.setY(state.getY() - 10);


        }

        return state.getY();


    }
}



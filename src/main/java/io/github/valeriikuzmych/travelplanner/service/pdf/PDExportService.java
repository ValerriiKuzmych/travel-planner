package io.github.valeriikuzmych.travelplanner.service.pdf;

import io.github.valeriikuzmych.travelplanner.dto.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class PDExportService implements IPDExportService {


    @Override
    public byte[] exportTripPlanToPdf(TripPlanDTO plan) throws IOException {

        try (PDDocument document = new PDDocument()) {

            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            PdfState state = new PdfState(page, contentStream, 750);

            state.setY(writeTitle(state, plan));

            state.setY(writeWeather(document, state, plan));

            state.setY(writeActivity(document, state, plan));


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

            PDPageContentStream newContentStream = new PDPageContentStream(document, newPage,
                    PDPageContentStream.AppendMode.APPEND,
                    true);

            state.setPage(newPage);
            state.setContentStream(newContentStream);
            state.setY(750);

        }

        return state;
    }

    private int writeTitle(PdfState state, TripPlanDTO plan) throws IOException {

        var cs = state.getContentStream();

        cs.beginText();
        cs.setFont(PDType1Font.HELVETICA_BOLD, 18);
        cs.newLineAtOffset(50, state.getY());
        cs.showText("Trip Plan: " + plan.getCity());
        cs.endText();

        return state.getY() - 40;

    }

    private int writeWeather(PDDocument document, PdfState state, TripPlanDTO plan) throws IOException {

        var cs = state.getContentStream();

        cs.beginText();
        cs.setFont(PDType1Font.HELVETICA_BOLD, 14);
        cs.newLineAtOffset(50, state.getY());
        cs.showText("Weather Forecast:");
        cs.endText();

        state.setY(state.getY() - 25);

        for (Map.Entry<LocalDate, WeatherDayDTO> entry
                : plan.getWeather().entrySet()) {

            state = checkPage(document, state);

            cs = state.getContentStream();
            cs.beginText();
            cs.setFont(PDType1Font.HELVETICA_BOLD, 12);
            cs.newLineAtOffset(60, state.getY());
            cs.showText(entry.getKey().toString());
            cs.endText();

            state.setY(state.getY() - 16);

            for (WeatherTimeDTO wt : entry.getValue().getTimes()) {

                state = checkPage(document, state);
                cs = state.getContentStream();

                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA, 12);
                cs.newLineAtOffset(80, state.getY());


                cs.showText(
                        wt.getTime()
                                + " — "
                                + wt.getTemperature()
                                + " °C, "
                                + wt.getDescription()
                );
                cs.endText();

                state.setY(state.getY() - 14);
            }

            state.setY(state.getY() - 10);
        }

        return state.getY() - 10;
    }

    private int writeActivity(PDDocument document, PdfState state, TripPlanDTO plan) throws IOException {

        var cs = state.getContentStream();

        cs.beginText();
        cs.setFont(PDType1Font.HELVETICA_BOLD, 14);
        cs.newLineAtOffset(50, state.getY());
        cs.showText("Activities:");
        cs.endText();

        state.setY(state.getY() - 25);


        for (Map.Entry<LocalDate, List<ActivityDTO>> entry : plan.getActivities().entrySet()) {

            state = checkPage(document, state);

            cs = state.getContentStream();
            cs.beginText();
            cs.setFont(PDType1Font.HELVETICA_BOLD, 12);
            cs.newLineAtOffset(60, state.getY());
            cs.showText(entry.getKey().toString() + ":");
            cs.endText();

            state.setY(state.getY() - 16);

            for (ActivityDTO act : entry.getValue()) {

                cs = state.getContentStream();
                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA, 12);
                cs.newLineAtOffset(80, state.getY());
                cs.showText(act.getStartTime() + " — " + act.getName());
                cs.endText();

                state.setY(state.getY() - 14);


            }

            state.setY(state.getY() - 10);


        }

        return state.getY();


    }
}



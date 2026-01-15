package io.github.valeriikuzmych.travelplanner.service.pdf;

import io.github.valeriikuzmych.travelplanner.dto.TripPlanDTO;
import io.github.valeriikuzmych.travelplanner.dto.activity.ActivityDTO;
import io.github.valeriikuzmych.travelplanner.dto.weather.WeatherDayDTO;
import io.github.valeriikuzmych.travelplanner.dto.weather.WeatherPeriodDTO;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
public class PDExportService implements IPDExportService {

    private static final int PAGE_HEIGHT = 842;
    private static final int MARGIN = 40;

    private static final int COLUMNS = 3;
    private static final int CARD_WIDTH = 165;
    private static final int CARD_HEIGHT = 200;
    private static final int CARD_GAP = 15;

    private static final int START_Y = PAGE_HEIGHT - 80;

    @Override
    public byte[] exportTripPlanToPdf(TripPlanDTO plan) throws IOException {

        try (PDDocument document = new PDDocument()) {

            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream cs = new PDPageContentStream(document, page);
            PdfState state = new PdfState(page, cs, START_Y);

            writeHeader(state, plan);

            int col = 0;
            int rowY = state.getY();

            for (LocalDate date : plan.getTripDates()) {

                WeatherDayDTO weather = plan.getWeather().get(date);
                List<ActivityDTO> activities = plan.getActivities().get(date);


                if (col == COLUMNS) {
                    col = 0;
                    rowY -= CARD_HEIGHT + CARD_GAP;
                }

                if (rowY < MARGIN + CARD_HEIGHT) {
                    cs.close();
                    page = new PDPage();
                    document.addPage(page);
                    cs = new PDPageContentStream(document, page);
                    state.setPage(page);
                    state.setContentStream(cs);
                    rowY = START_Y;
                }

                int x = MARGIN + col * (CARD_WIDTH + CARD_GAP);

                drawDayCard(cs, x, rowY, date, weather, activities);

                col++;
            }

            cs.close();

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                document.save(baos);
                return baos.toByteArray();
            }
        }
    }

    private void writeHeader(PdfState state, TripPlanDTO plan) throws IOException {

        var cs = state.getContentStream();


        cs.beginText();
        cs.setFont(PDType1Font.HELVETICA_BOLD, 22);
        cs.newLineAtOffset(MARGIN, state.getY());
        cs.showText("Trip: " + plan.getCity());
        cs.endText();

        state.setY(state.getY() - 28);


        String dateRange =
                formatDayMonth(plan.getStartDate())
                        + " – "
                        + formatDayMonth(plan.getEndDate())
                        + " "
                        + plan.getEndDate().getYear();

        cs.beginText();
        cs.setFont(PDType1Font.HELVETICA, 12);
        cs.newLineAtOffset(MARGIN, state.getY());
        cs.showText(dateRange);
        cs.endText();

        state.setY(state.getY() - 35);
    }

    private void drawDayCard(
            PDPageContentStream cs,
            int x,
            int y,
            LocalDate date,
            WeatherDayDTO weather,
            List<ActivityDTO> activities
    ) throws IOException {

        cs.setNonStrokingColor(245, 245, 245);
        cs.addRect(x, y - CARD_HEIGHT, CARD_WIDTH, CARD_HEIGHT);
        cs.fill();
        cs.setNonStrokingColor(0, 0, 0);

        int cursorY = y - 15;


        cs.beginText();
        cs.setFont(PDType1Font.HELVETICA_BOLD, 12);
        cs.newLineAtOffset(x + 10, cursorY);
        cs.showText(
                date.getDayOfWeek().name().substring(0, 3)
                        + " · "
                        + formatDayMonth(date)
        );
        cs.endText();

        cursorY -= 18;


        if (weather != null) {
            for (WeatherPeriodDTO p : weather.getPeriods()) {
                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA, 9);
                cs.newLineAtOffset(x + 10, cursorY);
                cs.showText(
                        p.getPeriod().getLabel()
                                + ": "
                                + p.getTemperature()
                                + "° · "
                                + p.getDescription()
                );
                cs.endText();
                cursorY -= 11;
            }
        }

        cursorY -= 6;


        if (activities != null) {
            int count = 0;
            for (ActivityDTO a : activities) {
                if (count == 4) break;

                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA, 9);
                cs.newLineAtOffset(x + 10, cursorY);
                cs.showText(a.getStartTime() + " " + a.getName());
                cs.endText();

                cursorY -= 11;
                count++;
            }
        }
    }

    private String formatDayMonth(LocalDate date) {
        return String.format(
                "%02d %s",
                date.getDayOfMonth(),
                date.getMonth().name().substring(0, 3)
        );
    }
}
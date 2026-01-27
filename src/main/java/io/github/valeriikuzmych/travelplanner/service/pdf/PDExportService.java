package io.github.valeriikuzmych.travelplanner.service.pdf;

import io.github.valeriikuzmych.travelplanner.dto.TripPlanDTO;
import io.github.valeriikuzmych.travelplanner.dto.activity.ActivityDTO;
import io.github.valeriikuzmych.travelplanner.dto.weather.WeatherDayDTO;
import io.github.valeriikuzmych.travelplanner.dto.weather.WeatherPeriodDTO;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class PDExportService implements IPDExportService {


    private static final int PAGE_WIDTH = 595;
    private static final int PAGE_HEIGHT = 842;

    private static final int MARGIN = 40;

    private static final int HEADER_HEIGHT = 90;
    private static final int CONTENT_START_Y = PAGE_HEIGHT - MARGIN - HEADER_HEIGHT;
    private static final int CONTENT_BOTTOM_Y = MARGIN;


    private static final int COLUMNS = 3;
    private static final int CARD_WIDTH = 165;
    private static final int CARD_HEIGHT = 200;
    private static final int CARD_GAP = 15;

    @Override
    public byte[] exportTripPlanToPdf(TripPlanDTO plan) throws IOException {

        try (PDDocument document = new PDDocument()) {

            PDType0Font fontRegular = loadFont(document, "/fonts/NotoSans-Regular.ttf");
            PDType0Font fontBold = loadFont(document, "/fonts/NotoSans-Bold.ttf");

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream cs = new PDPageContentStream(document, page);

            drawHeader(cs, plan, fontBold, fontRegular);

            int col = 0;
            int rowY = CONTENT_START_Y;

            for (LocalDate date : plan.getTripDates()) {

                if (col == COLUMNS) {
                    col = 0;
                    rowY -= CARD_HEIGHT + CARD_GAP;
                }

                if (rowY - CARD_HEIGHT < CONTENT_BOTTOM_Y) {
                    cs.close();

                    page = new PDPage(PDRectangle.A4);
                    document.addPage(page);
                    cs = new PDPageContentStream(document, page);

                    col = 0;
                    rowY = CONTENT_START_Y;
                }

                int x = MARGIN + col * (CARD_WIDTH + CARD_GAP);

                drawDayCard(
                        cs,
                        x,
                        rowY,
                        date,
                        plan.getWeather().get(date),
                        plan.getActivities().get(date),
                        fontBold,
                        fontRegular
                );

                col++;
            }

            cs.close();

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                document.save(baos);
                return baos.toByteArray();
            }
        }
    }


    private void drawHeader(
            PDPageContentStream cs,
            TripPlanDTO plan,
            PDType0Font fontBold,
            PDType0Font fontRegular
    ) throws IOException {

        int y = PAGE_HEIGHT - MARGIN - 30;

        cs.beginText();
        cs.setFont(fontBold, 22);
        cs.newLineAtOffset(MARGIN, y);
        cs.showText("Trip: " + plan.getCity());
        cs.endText();

        y -= 26;

        String range =
                formatDayMonth(plan.getStartDate()) +
                        " – " +
                        formatDayMonth(plan.getEndDate()) +
                        " " +
                        plan.getEndDate().getYear();

        cs.beginText();
        cs.setFont(fontRegular, 12);
        cs.newLineAtOffset(MARGIN, y);
        cs.showText(range);
        cs.endText();

        cs.setLineWidth(0.5f);
        cs.moveTo(MARGIN, PAGE_HEIGHT - MARGIN - HEADER_HEIGHT + 10);
        cs.lineTo(PAGE_WIDTH - MARGIN, PAGE_HEIGHT - MARGIN - HEADER_HEIGHT + 10);
        cs.stroke();
    }


    private void drawDayCard(
            PDPageContentStream cs,
            int x,
            int y,
            LocalDate date,
            WeatherDayDTO weather,
            List<ActivityDTO> activities,
            PDType0Font fontBold,
            PDType0Font fontRegular
    ) throws IOException {

        cs.setNonStrokingColor(245, 245, 245);
        cs.addRect(x, y - CARD_HEIGHT, CARD_WIDTH, CARD_HEIGHT);
        cs.fill();
        cs.setNonStrokingColor(0, 0, 0);

        int cursorY = y - 18;

        cs.beginText();
        cs.setFont(fontBold, 12);
        cs.newLineAtOffset(x + 10, cursorY);
        cs.showText(
                date.getDayOfWeek().name().substring(0, 3)
                        + " · " +
                        formatDayMonth(date)
        );
        cs.endText();

        cursorY -= 18;


        if (weather != null) {
            for (WeatherPeriodDTO p : weather.getPeriods()) {
                cs.beginText();
                cs.setFont(fontRegular, 9);
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
            for (ActivityDTO a : activities) {

                int titleLines =
                        drawWrappedText(
                                cs,
                                a.getStartTime() + " " + a.getName(),
                                x + 10,
                                cursorY,
                                CARD_WIDTH - 20,
                                fontRegular,
                                9,
                                11
                        );

                cursorY -= titleLines * 11 + 4;

                if (a.getNote() != null && !a.getNote().isBlank()) {

                    int noteLines =
                            drawWrappedText(
                                    cs,
                                    a.getNote(),
                                    x + 14,
                                    cursorY,
                                    CARD_WIDTH - 28,
                                    fontRegular,
                                    8,
                                    10
                            );

                    cursorY -= noteLines * 10 + 6;
                }
            }
        }

        drawNoteLines(cs, x, cursorY - 4, y - CARD_HEIGHT + 14);
    }

    private void drawNoteLines(PDPageContentStream cs, int x, int fromY, int toY) throws IOException {

        cs.setStrokingColor(200, 200, 200);
        cs.setLineWidth(0.5f);
        cs.setLineDashPattern(new float[]{2, 2}, 0);

        int y = fromY;
        while (y > toY) {
            cs.moveTo(x + 10, y);
            cs.lineTo(x + CARD_WIDTH - 10, y);
            cs.stroke();
            y -= 12;
        }

        cs.setLineDashPattern(new float[]{}, 0);
        cs.setStrokingColor(0, 0, 0);
    }


    private PDType0Font loadFont(PDDocument document, String path) throws IOException {
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is == null) {
                throw new IllegalStateException("Font not found: " + path);
            }
            return PDType0Font.load(document, is, true);
        }
    }

    private String formatDayMonth(LocalDate date) {
        return String.format(
                "%02d %s",
                date.getDayOfMonth(),
                date.getMonth().name().substring(0, 3)
        );
    }

    private int drawWrappedText(
            PDPageContentStream cs,
            String text,
            float x,
            float startY,
            float maxWidth,
            PDType0Font font,
            float fontSize,
            float lineHeight
    ) throws IOException {

        List<String> lines = new ArrayList<>();
        StringBuilder currentLine = new StringBuilder();

        for (String word : text.split(" ")) {

            float wordWidth =
                    font.getStringWidth(word) / 1000 * fontSize;

            if (wordWidth > maxWidth) {

                for (char c : word.toCharArray()) {
                    String test = currentLine.toString() + c;
                    float testWidth =
                            font.getStringWidth(test) / 1000 * fontSize;

                    if (testWidth > maxWidth) {
                        lines.add(currentLine.toString());
                        currentLine = new StringBuilder(String.valueOf(c));
                    } else {
                        currentLine.append(c);
                    }
                }
                continue;
            }

            String testLine =
                    currentLine.isEmpty()
                            ? word
                            : currentLine + " " + word;

            float testWidth =
                    font.getStringWidth(testLine) / 1000 * fontSize;

            if (testWidth > maxWidth) {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder(word);
            } else {
                currentLine = new StringBuilder(testLine);
            }
        }

        if (!currentLine.isEmpty()) {
            lines.add(currentLine.toString());
        }

        float y = startY;

        for (String line : lines) {
            cs.beginText();
            cs.setFont(font, fontSize);
            cs.newLineAtOffset(x, y);
            cs.showText(line);
            cs.endText();

            y -= lineHeight;
        }

        return lines.size();
    }

}
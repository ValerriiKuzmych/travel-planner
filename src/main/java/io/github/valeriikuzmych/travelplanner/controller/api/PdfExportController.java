package io.github.valeriikuzmych.travelplanner.controller.api;

import io.github.valeriikuzmych.travelplanner.dto.TripPlanDTO;
import io.github.valeriikuzmych.travelplanner.service.TripPlannerService;
import io.github.valeriikuzmych.travelplanner.service.pdf.IPDExportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/trips")
public class PdfExportController {

    private final IPDExportService pdfExportService;
    private final TripPlannerService tripPlannerService;


    public PdfExportController(IPDExportService pdfExportService,
                               TripPlannerService tripPlannerService) {

        this.pdfExportService = pdfExportService;
        this.tripPlannerService = tripPlannerService;

    }


    @GetMapping("/{id}/plan/pdf")
    public ResponseEntity<byte[]> exportTripPlan(@PathVariable Long id, Principal principal)
            throws IOException {

        TripPlanDTO dto = tripPlannerService.getPlanForTrip(id, principal.getName());

        try {

            byte[] pdf = pdfExportService.exportTripPlanToPdf(dto);

            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    .header("Content-Disposition", "attachment; filename=trip-plan-" + id + ".pdf")
                    .body(pdf);

        } catch (IOException e) {

            throw new IllegalStateException("Failed to generate PDF");
        }

    }


}

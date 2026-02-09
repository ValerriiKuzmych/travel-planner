package io.github.valeriikuzmych.travelplanner.service.pdf;

import io.github.valeriikuzmych.travelplanner.dto.TripPlanDTO;

import java.io.IOException;

public interface IPdfExportService {

    byte[] exportTripPlanToPdf(TripPlanDTO plan) throws IOException;

}

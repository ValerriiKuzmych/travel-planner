package io.github.valeriikuzmych.travelplanner.controller;


import io.github.valeriikuzmych.travelplanner.dto.TripPlanDTO;
import io.github.valeriikuzmych.travelplanner.service.ITripPlannerService;
import io.github.valeriikuzmych.travelplanner.service.pdf.IPDExportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PdfExportControllerTest {


    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    IPDExportService pdfExportService;

    @MockitoBean
    ITripPlannerService tripPlannerService;


    @Test
    @WithMockUser
    void exportPdf_success() throws Exception {

        TripPlanDTO dto = new TripPlanDTO();
        dto.setCity("Rome");

        when(tripPlannerService.getPlanForTrip(1L)).thenReturn(dto);
        when(pdfExportService.exportTripPlanToPdf(dto)).thenReturn(new byte[]{1, 2, 3});

        mockMvc.perform(get("/trips/1/plan/pdf"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().string("Content-Disposition", "attachment; filename=trip-plan-1.pdf"));
    }

}

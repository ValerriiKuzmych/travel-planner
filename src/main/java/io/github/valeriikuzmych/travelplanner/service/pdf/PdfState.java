package io.github.valeriikuzmych.travelplanner.service.pdf;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

public class PdfState {

    private PDPageContentStream contentStream;
    private PDPage page;
    private int y;

    public PdfState(PDPage page, PDPageContentStream contentStream, int y) {
        this.page = page;
        this.contentStream = contentStream;
        this.y = y;
    }

    public PDPageContentStream getContentStream() {
        return contentStream;
    }

    public void setContentStream(PDPageContentStream contentStream) {
        this.contentStream = contentStream;
    }

    public PDPage getPage() {
        return page;
    }

    public void setPage(PDPage page) {
        this.page = page;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}

package com.pdfmerger.app.infrastructure.pdf;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDPageLabelRange;
import org.apache.pdfbox.pdmodel.common.PDPageLabels;
import org.junit.jupiter.api.Test;

class PdfBoxProcessingGatewayTest {

    private final PdfBoxProcessingGateway pdfBoxProcessingGateway = new PdfBoxProcessingGateway();

    @Test
    void shouldResetPageLabelsToSequentialValuesAfterMerge() throws IOException {
        byte[] firstPdf = createPdfWithRestartedPageLabels();
        byte[] secondPdf = createPdfWithRestartedPageLabels();

        byte[] mergedPdf = pdfBoxProcessingGateway.mergePdfs(List.of(firstPdf, secondPdf));

        try (PDDocument mergedDocument = PDDocument.load(mergedPdf)) {
            PDPageLabels pageLabels = mergedDocument.getDocumentCatalog().getPageLabels();
            assertNotNull(pageLabels);
            assertEquals(4, mergedDocument.getNumberOfPages());
            assertArrayEquals(new String[] { "1", "2", "3", "4" }, pageLabels.getLabelsByPageIndices());
        }
    }

    private byte[] createPdfWithRestartedPageLabels() throws IOException {
        try (PDDocument document = new PDDocument();
                ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            document.addPage(new PDPage());
            document.addPage(new PDPage());

            PDPageLabels pageLabels = new PDPageLabels(document);

            PDPageLabelRange firstRange = new PDPageLabelRange();
            firstRange.setStyle(PDPageLabelRange.STYLE_DECIMAL);
            firstRange.setStart(1);
            pageLabels.setLabelItem(0, firstRange);

            PDPageLabelRange secondRange = new PDPageLabelRange();
            secondRange.setStyle(PDPageLabelRange.STYLE_DECIMAL);
            secondRange.setStart(1);
            pageLabels.setLabelItem(1, secondRange);

            document.getDocumentCatalog().setPageLabels(pageLabels);
            document.save(output);
            return output.toByteArray();
        }
    }
}

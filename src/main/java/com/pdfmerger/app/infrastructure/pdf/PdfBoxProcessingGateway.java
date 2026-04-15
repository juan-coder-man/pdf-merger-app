package com.pdfmerger.app.infrastructure.pdf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Component;

import com.pdfmerger.app.exception.PdfProcessingException;

/**
 * Implementacion con PDFBox para unificar documentos y numerar paginas.
 */
@Component
public class PdfBoxProcessingGateway implements PdfProcessingGateway {

    private static final float PAGE_NUMBER_FONT_SIZE = 10f;
    private static final float BOTTOM_MARGIN = 16f;

    /**
     * Une los documentos en el orden recibido y agrega numeracion final.
     *
     * @param orderedPdfs lista de PDFs en el orden de salida.
     * @return bytes del documento unificado.
     */
    @Override
    public byte[] mergeAndNumberPages(List<byte[]> orderedPdfs) {
        PDFMergerUtility merger = new PDFMergerUtility();

        for (byte[] pdfBytes : orderedPdfs) {
            merger.addSource(new ByteArrayInputStream(pdfBytes));
        }

        try (ByteArrayOutputStream mergedOutput = new ByteArrayOutputStream()) {
            merger.setDestinationStream(mergedOutput);
            merger.mergeDocuments(null);

            try (PDDocument mergedDocument = PDDocument.load(mergedOutput.toByteArray());
                    ByteArrayOutputStream finalOutput = new ByteArrayOutputStream()) {

                addPageNumbers(mergedDocument);
                mergedDocument.save(finalOutput);
                return finalOutput.toByteArray();
            }
        } catch (IOException ex) {
            throw new PdfProcessingException("Error al unificar los archivos PDF", ex);
        }
    }

    /**
     * Escribe el numero de pagina centrado en la parte inferior de cada hoja.
     */
    private void addPageNumbers(PDDocument document) throws IOException {
        int totalPages = document.getNumberOfPages();

        for (int i = 0; i < totalPages; i++) {
            PDPage page = document.getPage(i);
            String pageNumber = String.valueOf(i + 1);
            PDRectangle mediaBox = page.getMediaBox();
            float textWidth = PDType1Font.HELVETICA.getStringWidth(pageNumber) / 1000f * PAGE_NUMBER_FONT_SIZE;
            float x = (mediaBox.getWidth() - textWidth) / 2f;
            float y = BOTTOM_MARGIN;

            try (PDPageContentStream contentStream = new PDPageContentStream(
                    document,
                    page,
                    PDPageContentStream.AppendMode.APPEND,
                    true,
                    true)) {
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, PAGE_NUMBER_FONT_SIZE);
                contentStream.newLineAtOffset(x, y);
                contentStream.showText(pageNumber);
                contentStream.endText();
            }
        }
    }
}

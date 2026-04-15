package com.pdfmerger.app.infrastructure.pdf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.springframework.stereotype.Component;

import com.pdfmerger.app.exception.PdfProcessingException;

/**
 * Implementacion con PDFBox para unificar documentos.
 */
@Component
public class PdfBoxProcessingGateway implements PdfProcessingGateway {

    /**
     * Une los documentos en el orden recibido.
     *
     * @param orderedPdfs lista de PDFs en el orden de salida.
     * @return bytes del documento unificado.
     */
    @Override
    public byte[] mergePdfs(List<byte[]> orderedPdfs) {
        PDFMergerUtility merger = new PDFMergerUtility();

        for (byte[] pdfBytes : orderedPdfs) {
            merger.addSource(new ByteArrayInputStream(pdfBytes));
        }

        try (ByteArrayOutputStream mergedOutput = new ByteArrayOutputStream()) {
            merger.setDestinationStream(mergedOutput);
            merger.mergeDocuments(null);
            return mergedOutput.toByteArray();
        } catch (IOException ex) {
            throw new PdfProcessingException("Error al unificar los archivos PDF", ex);
        }
    }
}

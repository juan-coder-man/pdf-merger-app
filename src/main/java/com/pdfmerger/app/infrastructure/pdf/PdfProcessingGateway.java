package com.pdfmerger.app.infrastructure.pdf;

import java.util.List;

/**
 * Contrato para operaciones de unificacion y postproceso de PDFs.
 */
public interface PdfProcessingGateway {
    /**
     * Unifica los PDFs en el orden recibido y agrega numeracion de pagina.
     *
     * @param orderedPdfs documentos fuente ya ordenados.
     * @return bytes del PDF final procesado.
     */
    byte[] mergeAndNumberPages(List<byte[]> orderedPdfs);
}

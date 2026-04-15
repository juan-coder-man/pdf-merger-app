package com.pdfmerger.app.infrastructure.pdf;

import java.util.List;

/**
 * Contrato para operaciones de unificacion de PDFs.
 */
public interface PdfProcessingGateway {
    /**
     * Unifica los PDFs en el orden recibido.
     *
     * @param orderedPdfs documentos fuente ya ordenados.
     * @return bytes del PDF final unificado.
     */
    byte[] mergePdfs(List<byte[]> orderedPdfs);
}

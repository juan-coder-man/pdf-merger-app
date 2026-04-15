package com.pdfmerger.app.exception;

/**
 * Representa errores tecnicos durante la manipulacion de PDFs.
 */
public class PdfProcessingException extends RuntimeException {

    /**
     * Crea una excepcion de procesamiento con causa original.
     *
     * @param message mensaje tecnico del error.
     * @param cause   excepcion original.
     */
    public PdfProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}

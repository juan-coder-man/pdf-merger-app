package com.pdfmerger.app.exception;

/**
 * Representa errores de validacion de entrada para el flujo de unificacion.
 */
public class PdfValidationException extends RuntimeException {

    /**
     * Crea una excepcion de validacion con mensaje funcional.
     *
     * @param message detalle de la regla incumplida.
     */
    public PdfValidationException(String message) {
        super(message);
    }
}

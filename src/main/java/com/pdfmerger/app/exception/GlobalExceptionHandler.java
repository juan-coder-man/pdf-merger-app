package com.pdfmerger.app.exception;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.pdfmerger.app.model.ApiErrorResponse;

/**
 * Centraliza el mapeo de excepciones de negocio a respuestas HTTP.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Convierte errores de validacion en respuestas 400.
     */
    @ExceptionHandler(PdfValidationException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(PdfValidationException ex) {
        ApiErrorResponse body = new ApiErrorResponse("VALIDATION_ERROR", ex.getMessage(), Instant.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Convierte errores internos de procesamiento en respuestas 500.
     */
    @ExceptionHandler(PdfProcessingException.class)
    public ResponseEntity<ApiErrorResponse> handleProcessing(PdfProcessingException ex) {
        ApiErrorResponse body = new ApiErrorResponse("PROCESSING_ERROR", ex.getMessage(), Instant.now());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}

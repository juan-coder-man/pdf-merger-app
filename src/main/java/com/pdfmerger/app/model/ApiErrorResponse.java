package com.pdfmerger.app.model;

import java.time.Instant;

/**
 * Estructura estandar de error retornada por la API.
 */
public record ApiErrorResponse(String code, String message, Instant timestamp) {
}

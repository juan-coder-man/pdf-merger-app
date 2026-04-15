package com.pdfmerger.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la aplicacion Spring Boot.
 */
@SpringBootApplication
public class PdfMergerApplication {

    /**
     * Inicializa el contexto de la aplicacion.
     *
     * @param args argumentos de inicio.
     */
    public static void main(String[] args) {
        SpringApplication.run(PdfMergerApplication.class, args);
    }
}

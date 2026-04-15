package com.pdfmerger.app.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

/**
 * Define el caso de uso para unificar PDFs subidos por el cliente.
 */
public interface PdfMergeService {
    /**
     * Valida, ordena y unifica archivos PDF.
     *
     * @param files archivos recibidos desde la solicitud.
     * @param order orden solicitado por indice.
     * @return PDF unificado como arreglo de bytes.
     */
    byte[] merge(List<MultipartFile> files, List<Integer> order);
}

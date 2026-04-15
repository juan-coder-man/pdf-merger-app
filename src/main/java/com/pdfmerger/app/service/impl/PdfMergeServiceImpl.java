package com.pdfmerger.app.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.pdfmerger.app.exception.PdfValidationException;
import com.pdfmerger.app.infrastructure.pdf.PdfProcessingGateway;
import com.pdfmerger.app.service.PdfMergeService;

/**
 * Implementa la orquestacion del flujo de validacion y unificacion de PDFs.
 */
@Service
public class PdfMergeServiceImpl implements PdfMergeService {

    private static final long MAX_TOTAL_SIZE_BYTES = 50L * 1024L * 1024L;

    private final PdfProcessingGateway pdfProcessingGateway;

    public PdfMergeServiceImpl(PdfProcessingGateway pdfProcessingGateway) {
        this.pdfProcessingGateway = pdfProcessingGateway;
    }

    /**
     * Ejecuta el flujo principal: valida entradas, resuelve orden y delega
     * procesamiento.
     */
    @Override
    public byte[] merge(List<MultipartFile> files, List<Integer> order) {
        validateFiles(files);
        List<Integer> resolvedOrder = resolveOrder(files.size(), order);

        List<byte[]> orderedPdfs = new ArrayList<>();
        for (Integer index : resolvedOrder) {
            MultipartFile file = files.get(index);
            try {
                orderedPdfs.add(file.getBytes());
            } catch (IOException ex) {
                throw new PdfValidationException("No se pudo leer uno de los archivos PDF");
            }
        }

        return pdfProcessingGateway.mergePdfs(orderedPdfs);
    }

    /**
     * Aplica reglas basicas de entrada para archivos PDF y tamano total permitido.
     */
    private void validateFiles(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new PdfValidationException("Debes subir al menos un archivo PDF");
        }

        long totalSize = 0;
        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                throw new PdfValidationException("Todos los archivos deben ser PDFs validos");
            }

            String filename = file.getOriginalFilename();
            if (filename == null || !filename.toLowerCase().endsWith(".pdf")) {
                throw new PdfValidationException("Solo se permiten archivos con extension .pdf");
            }

            totalSize += file.getSize();
        }

        if (totalSize > MAX_TOTAL_SIZE_BYTES) {
            throw new PdfValidationException("El tamano total de archivos excede el limite permitido");
        }
    }

    /**
     * Determina el orden final de procesamiento y valida su consistencia.
     */
    private List<Integer> resolveOrder(int fileCount, List<Integer> order) {
        if (order == null || order.isEmpty()) {
            List<Integer> defaultOrder = new ArrayList<>();
            for (int i = 0; i < fileCount; i++) {
                defaultOrder.add(i);
            }
            return defaultOrder;
        }

        if (order.size() != fileCount) {
            throw new PdfValidationException("El orden recibido no coincide con la cantidad de archivos");
        }

        Set<Integer> unique = new HashSet<>(order);
        if (unique.size() != fileCount) {
            throw new PdfValidationException("El orden contiene indices duplicados");
        }

        for (Integer index : order) {
            if (index == null || index < 0 || index >= fileCount) {
                throw new PdfValidationException("El orden contiene indices fuera de rango");
            }
        }

        return order;
    }
}

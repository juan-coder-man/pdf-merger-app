package com.pdfmerger.app.service;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import com.pdfmerger.app.exception.PdfValidationException;
import com.pdfmerger.app.infrastructure.pdf.PdfProcessingGateway;
import com.pdfmerger.app.service.impl.PdfMergeServiceImpl;

/**
 * Pruebas unitarias para validar reglas de negocio del servicio de merge.
 */
@ExtendWith(MockitoExtension.class)
class PdfMergeServiceImplTest {

    @Mock
    private PdfProcessingGateway pdfProcessingGateway;

    @InjectMocks
    private PdfMergeServiceImpl pdfMergeService;

    @Test
    void shouldMergeFilesUsingProvidedOrder() {
        MockMultipartFile first = new MockMultipartFile("files", "first.pdf", "application/pdf", "A".getBytes());
        MockMultipartFile second = new MockMultipartFile("files", "second.pdf", "application/pdf", "B".getBytes());
        byte[] expected = "merged".getBytes();

        when(pdfProcessingGateway.mergePdfs(anyList())).thenReturn(expected);

        byte[] result = pdfMergeService.merge(List.of(first, second), List.of(1, 0));

        assertArrayEquals(expected, result);
        verify(pdfProcessingGateway).mergePdfs(argThat(orderedPdfs -> orderedPdfs.size() == 2
                && java.util.Arrays.equals(orderedPdfs.get(0), "B".getBytes())
                && java.util.Arrays.equals(orderedPdfs.get(1), "A".getBytes())));
    }

    @Test
    void shouldFailWhenOrderSizeDoesNotMatchFiles() {
        MockMultipartFile first = new MockMultipartFile("files", "first.pdf", "application/pdf", "A".getBytes());
        MockMultipartFile second = new MockMultipartFile("files", "second.pdf", "application/pdf", "B".getBytes());

        assertThrows(PdfValidationException.class, () -> pdfMergeService.merge(List.of(first, second), List.of(0)));
    }

    @Test
    void shouldFailWhenAnyFileIsNotPdf() {
        MockMultipartFile bad = new MockMultipartFile("files", "notes.txt", "text/plain", "TXT".getBytes());

        assertThrows(PdfValidationException.class, () -> pdfMergeService.merge(List.of(bad), List.of(0)));
    }
}

package com.pdfmerger.app.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.ByteArrayOutputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/**
 * Pruebas de integracion del endpoint HTTP de unificacion.
 */
@SpringBootTest
@AutoConfigureMockMvc
class PdfMergeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldMergeAndReturnPdf() throws Exception {
        MockMultipartFile fileOne = new MockMultipartFile("files", "one.pdf", "application/pdf", createSinglePagePdf());
        MockMultipartFile fileTwo = new MockMultipartFile("files", "two.pdf", "application/pdf", createSinglePagePdf());

        MvcResult result = mockMvc.perform(
                multipart("/api/pdf/merge")
                        .file(fileOne)
                        .file(fileTwo)
                        .param("order", "0", "1")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=merged.pdf"))
                .andReturn();

        byte[] responseBytes = result.getResponse().getContentAsByteArray();
        assertThat(responseBytes).isNotEmpty();

        try (PDDocument document = PDDocument.load(responseBytes)) {
            assertThat(document.getNumberOfPages()).isEqualTo(2);
        }
    }

    @Test
    void shouldReturnBadRequestWhenNoFiles() throws Exception {
        mockMvc.perform(multipart("/api/pdf/merge").param("order", "0"))
                .andExpect(status().isBadRequest());
    }

    /**
     * Genera un PDF simple de una pagina para escenarios de prueba.
     */
    private byte[] createSinglePagePdf() throws Exception {
        try (PDDocument document = new PDDocument(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            document.addPage(new PDPage());
            document.save(output);
            return output.toByteArray();
        }
    }
}

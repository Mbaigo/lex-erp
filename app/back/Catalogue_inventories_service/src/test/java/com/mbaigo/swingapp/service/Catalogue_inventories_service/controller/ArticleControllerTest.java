package com.mbaigo.swingapp.service.Catalogue_inventories_service.controller;

import com.mbaigo.swingapp.service.Catalogue_inventories_service.service.ArticleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ArticleController.class)
@AutoConfigureMockMvc(addFilters = false)
class ArticleControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private ArticleService articleService;

    @Test
    @DisplayName("PATCH /stock doit retourner 200 OK")
    void updateStock_shouldReturnOk() throws Exception {
        mockMvc.perform(patch("/api/v1/articles/REF-01/stock")
                        .param("quantite", "5.0")
                        .param("isDebit", "true"))
                .andExpect(status().isOk());

        verify(articleService).updateStock("REF-01", 5.0, true);
    }
}


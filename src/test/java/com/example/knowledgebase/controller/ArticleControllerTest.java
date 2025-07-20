package com.example.knowledgebase.controller;

import com.example.knowledgebase.dto.ArticleDto;
import com.example.knowledgebase.service.ArticleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ArticleController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ArticleControllerTest.Config.class)
class ArticleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ArticleService articleService;

    @TestConfiguration
    static class Config {
        @Bean
        public ArticleService articleService() {
            return mock(ArticleService.class);
        }
    }

    @BeforeEach
    void setupMock() {
        reset(articleService);
    }

    @Test
    void getMesArticles_shouldReturnList() throws Exception {
        ArticleDto article = new ArticleDto();
        article.setTitre("Titre");
        article.setContenu("Contenu");
        article.setStatus("EN_ATTENTE");
        article.setAuteur("auteur");

        List<ArticleDto> articles = List.of(article);
        when(articleService.getByAuthor("admin")).thenReturn(articles);

        mockMvc.perform(get("/api/articles/mes-articles")
                        .param("username", "admin")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
}

package com.example.knowledgebase.mapper;

import com.example.knowledgebase.dto.ArticleDto;
import com.example.knowledgebase.model.Article;
import com.example.knowledgebase.model.ArticleStatus;
import com.example.knowledgebase.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ArticleMapperTest {

    private final ArticleMapper articleMapper = new ArticleMapper();

    @Test
    void toDto_shouldMapFieldsCorrectly() {
        // Préparation de l'entité Article
        Article article = new Article();
        article.setArticleId(1L);
        article.setTitre("Titre");
        article.setContenu("Contenu");
        article.setStatus(ArticleStatus.EN_ATTENTE);
        article.setCreationDate(LocalDateTime.now());
        article.setDraft(false);

        User author = new User();
        author.setUsername("contributor");
        article.setAuthor(author);

        // Appel de la méthode à tester
        ArticleDto dto = articleMapper.toDto(article);

        // Vérifications
        assertEquals("Titre", dto.getTitre());
        assertEquals("Contenu", dto.getContenu());
        assertEquals("EN_ATTENTE", dto.getStatus());
        assertEquals("contributor", dto.getAuteur());
    }

    @Test
    void toEntity_shouldMapFieldsCorrectly() {
        // Préparation du DTO
        ArticleDto dto = new ArticleDto();
        dto.setTitre("Titre");
        dto.setContenu("Contenu");

        User author = new User();
        author.setUsername("contributor");

        // Appel de la méthode à tester
        Article entity = articleMapper.toEntity(dto, author);

        // Vérifications
        assertEquals("Titre", entity.getTitre());
        assertEquals("Contenu", entity.getContenu());
        assertEquals("contributor", entity.getAuthor().getUsername());
    }
}

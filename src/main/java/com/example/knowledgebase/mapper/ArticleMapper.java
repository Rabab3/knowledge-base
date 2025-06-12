package com.example.knowledgebase.mapper;

import com.example.knowledgebase.dto.ArticleDto;
import com.example.knowledgebase.model.Article;
import com.example.knowledgebase.model.ArticleStatus;
import com.example.knowledgebase.model.User;
import org.springframework.stereotype.Component;

@Component
public class ArticleMapper {

    public ArticleDto toDto(Article article) {
        ArticleDto dto = new ArticleDto();
        dto.setArticleId(article.getArticleId());
        dto.setTitle(article.getTitle());
        dto.setContent(article.getContent());
        dto.setCreationDate(article.getCreationDate());
        dto.setModificationDate(article.getModificationDate());
        dto.setAuthorEmail(article.getAuthor().getEmail());
        dto.setStatus(article.getStatus().name()); // ✅ statut converti en String
        return dto;
    }

    public Article toEntity(ArticleDto dto, User author) {
        Article article = new Article();
        article.setTitle(dto.getTitle());
        article.setContent(dto.getContent());
        article.setAuthor(author);

        // ⚠️ Si status est présent dans le DTO, le convertir ; sinon valeur par défaut
        if (dto.getStatus() != null) {
            article.setStatus(ArticleStatus.valueOf(dto.getStatus()));
        } else {
            article.setStatus(ArticleStatus.EN_ATTENTE); // ✅ valeur par défaut
        }

        return article;
    }
}

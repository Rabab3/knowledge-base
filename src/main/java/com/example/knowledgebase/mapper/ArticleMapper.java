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
        dto.setStatus(article.getStatus().name());  // ✅ enum to String
        dto.setIsDraft(article.isDraft());          // ✅ bool isDraft

        // ✅ Ajout de l'auteur (username)
        if (article.getAuthor() != null) {
            dto.setAuteur(article.getAuthor().getUsername());
        }

        return dto;
    }

    public Article toEntity(ArticleDto dto, User author) {
        Article article = new Article();
        article.setTitle(dto.getTitle());
        article.setContent(dto.getContent());
        article.setAuthor(author);

        // ✅ Le statut sera défini dans ArticleService.create()
        article.setStatus(ArticleStatus.EN_ATTENTE);
        article.setDraft(false);

        return article;
    }
}

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
        dto.setTitre(article.getTitre());
        dto.setContenu(article.getContenu());
        dto.setCreationDate(article.getCreationDate());
        dto.setModificationDate(article.getModificationDate());

        if (article.getStatus() != null) {
            dto.setStatus(article.getStatus().name());
        }

        dto.setIsDraft(article.isDraft());

        if (article.getAuthor() != null) {
            dto.setAuteur(article.getAuthor().getUsername());
        }

        return dto;
    }

    public Article toEntity(ArticleDto dto, User author) {
        Article article = new Article();
        article.setTitre(dto.getTitre());
        article.setContenu(dto.getContenu());
        article.setAuthor(author);
        article.setDraft(dto.isDraft());

        // ✅ Ajout d'une vérification sécurisée
        if (dto.getStatus() != null) {
            try {
                article.setStatus(ArticleStatus.valueOf(dto.getStatus()));
            } catch (IllegalArgumentException e) {
                System.out.println("⚠️ Statut inconnu : " + dto.getStatus());
                article.setStatus(ArticleStatus.EN_ATTENTE); // Valeur par défaut
            }
        } else {
            System.out.println("⚠️ Aucun statut reçu, valeur par défaut appliquée.");
            article.setStatus(ArticleStatus.EN_ATTENTE); // Valeur par défaut
        }

        return article;
    }
}

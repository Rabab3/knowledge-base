package com.example.knowledgebase.repository;

import com.example.knowledgebase.model.Article;
import com.example.knowledgebase.model.ArticleStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

public class ArticleSpecification {

    public static Specification<Article> hasTitre(String titre) {
        return (root, query, cb) ->
                StringUtils.hasText(titre) ? cb.like(cb.lower(root.get("titre")), "%" + titre.toLowerCase() + "%") : null;
    }

    public static Specification<Article> hasContenu(String contenu) {
        return (root, query, cb) ->
                StringUtils.hasText(contenu) ? cb.like(cb.lower(root.get("contenu")), "%" + contenu.toLowerCase() + "%") : null;
    }

    public static Specification<Article> hasAuteur(Long auteurId) {
        return (root, query, cb) ->
                auteurId != null ? cb.equal(root.get("author").get("id"), auteurId) : null;
    }

    public static Specification<Article> hasStatut(ArticleStatus status) {
        return (root, query, cb) ->
                status != null ? cb.equal(root.get("status"), status) : null;
    }

    public static Specification<Article> createdAfter(LocalDateTime date) {
        return (root, query, cb) ->
                date != null ? cb.greaterThanOrEqualTo(root.get("creationDate"), date) : null;
    }

    public static Specification<Article> createdBefore(LocalDateTime date) {
        return (root, query, cb) ->
                date != null ? cb.lessThanOrEqualTo(root.get("creationDate"), date) : null;
    }
}

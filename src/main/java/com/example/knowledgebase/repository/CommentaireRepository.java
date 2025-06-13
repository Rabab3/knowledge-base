package com.example.knowledgebase.repository;

import com.example.knowledgebase.model.Commentaire;
import com.example.knowledgebase.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentaireRepository extends JpaRepository<Commentaire, Long> {
    List<Commentaire> findByArticle(Article article);
}

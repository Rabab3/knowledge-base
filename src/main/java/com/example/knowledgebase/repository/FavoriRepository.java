package com.example.knowledgebase.repository;

import com.example.knowledgebase.model.Favori;
import com.example.knowledgebase.model.User;
import com.example.knowledgebase.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriRepository extends JpaRepository<Favori, Long> {
    List<Favori> findByUser(User user);
    Optional<Favori> findByUserAndArticle(User user, Article article);
}

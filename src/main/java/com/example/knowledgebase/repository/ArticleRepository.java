package com.example.knowledgebase.repository;

import com.example.knowledgebase.model.Article;
import com.example.knowledgebase.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findByAuthor(User author);
}

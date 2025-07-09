package com.example.knowledgebase.repository;

import com.example.knowledgebase.model.Article;
import com.example.knowledgebase.model.ArticleStatus;
import com.example.knowledgebase.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long>, JpaSpecificationExecutor<Article> {
    List<Article> findByAuthor(User author);
    List<Article> findByAuthorAndIsDraftTrue(User author);
    List<Article> findByStatus(ArticleStatus status);

}

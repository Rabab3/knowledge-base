package com.example.knowledgebase.repository;

import com.example.knowledgebase.model.ArticleVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleVersionRepository extends JpaRepository<ArticleVersion, Long> {
    List<ArticleVersion> findByArticleIdOrderBySauvegardéLeDesc(Long articleId);
}

package com.example.knowledgebase.dto;
import com.example.knowledgebase.model.ArticleStatus;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ArticleSearchRequest {
    private String titre;
    private String contenu;
    private Long auteurId;
    private ArticleStatus status;
    private LocalDateTime createdAfter;
    private LocalDateTime createdBefore;

}

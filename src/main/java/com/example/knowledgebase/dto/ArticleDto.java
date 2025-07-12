package com.example.knowledgebase.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ArticleDto {
    private String status;
    private Long articleId;
    private String titre;
    private String contenu;
    private LocalDateTime creationDate;
    private LocalDateTime modificationDate;
    private String authorEmail;
    private boolean isDraft;
    private String auteur;

    public void setIsDraft(boolean isDraft) {
        this.isDraft = isDraft;
    }
}

package com.example.knowledgebase.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ArticleDto {

    private String status;
    private Long articleId;

    @NotBlank(message = "Le titre est obligatoire")
        private String titre;

    @NotBlank(message = "Le contenu est obligatoire")
    private String contenu;

    private LocalDateTime creationDate;
    private LocalDateTime modificationDate;
    private String authorEmail;
    private boolean isDraft;
    private String auteur;
    private String retourCommentaire;


    public void setIsDraft(boolean isDraft) {
        this.isDraft = isDraft;
    }
}

package com.example.knowledgebase.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long articleId;

    @Enumerated(EnumType.STRING)
    private ArticleStatus status;

    @Column(name = "title")
    private String titre;

    @Column(columnDefinition = "TEXT", name = "content")
    private String contenu;

    @Column(nullable = false)
    private boolean isDraft = false;

    @Column(name = "file_name")
    private String fileName;

    private LocalDateTime creationDate;
    private LocalDateTime modificationDate;

    // ✅ Champs pour retour du modérateur
    @Column(name = "retour_commentaire")
    private String retourCommentaire;

    @Column(name = "date_retour")
    private LocalDateTime dateRetour;

    // ✅ Auteur de l'article
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;
}

package com.example.knowledgebase.model;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Article {

    @Enumerated(EnumType.STRING)
    private ArticleStatus status;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long articleId;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;
}



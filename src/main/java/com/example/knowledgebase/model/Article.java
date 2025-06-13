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

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private boolean isDraft = false;

    @Column(name = "file_name") // ✅ Ajout ici
    private String fileName;

    private LocalDateTime creationDate;
    private LocalDateTime modificationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;
}



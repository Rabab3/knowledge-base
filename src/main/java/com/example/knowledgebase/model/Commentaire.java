package com.example.knowledgebase.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Commentaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private LocalDateTime date;

    @ManyToOne(optional = false)
    private User author;

    @ManyToOne(optional = false)
    private Article article;
}

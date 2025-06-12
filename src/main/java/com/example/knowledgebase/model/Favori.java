package com.example.knowledgebase.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "favoris", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "article_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Favori {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User user;

    @ManyToOne(optional = false)
    private Article article;
}

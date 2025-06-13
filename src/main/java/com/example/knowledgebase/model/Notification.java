package com.example.knowledgebase.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    private LocalDateTime date;

    private boolean lu = false;

    @ManyToOne
    private User destinataire;

    @ManyToOne
    private Article article; // facultatif : pour contextualiser
}

package com.example.knowledgebase.model;

import jakarta.persistence.*;
import lombok.*;
import com.example.knowledgebase.model.ERole;  // Assurez-vous d'importer l'énumération ERole

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)  // Utilisation de l'énumération sous forme de chaîne
    @Column(length = 20, nullable = false, unique = true)
    private ERole name;

    public void setName(ERole name) {
        this.name = name;
    }
}

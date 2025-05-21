package com.example.knowledgebase.config;

import com.example.knowledgebase.model.ERole;  // Correction du package
import com.example.knowledgebase.model.Role;  // Correction du package
import com.example.knowledgebase.repository.RoleRepository;  // Correction du package
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        // Vérifie si chaque rôle est déjà présent dans la base de données avant de l'ajouter
        for (ERole roleName : ERole.values()) {
            if (roleRepository.findByName(roleName).isEmpty()) {
                roleRepository.save(new Role(null, roleName));
            }
        }
    }
}

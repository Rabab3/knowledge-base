package com.example.knowledgebase.config;

import com.example.knowledgebase.model.ERole;
import com.example.knowledgebase.model.Role;
import com.example.knowledgebase.model.User;
import com.example.knowledgebase.repository.RoleRepository;
import com.example.knowledgebase.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initializeData() {

        // Créer tous les rôles s'ils n'existent pas
        for (ERole roleName : ERole.values()) {
            if (roleRepository.findByName(roleName).isEmpty()) {
                roleRepository.save(new Role(null, roleName));
                System.out.println("Inserted role: " + roleName);
            }
        }

        // Créer l'utilisateur admin s'il n'existe pas
        if (!userRepository.existsByUsername("admin")) {
            Optional<Role> adminRole = roleRepository.findByName(ERole.ROLE_ADMIN);
            if (adminRole.isPresent()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setNom("Admin");
                admin.setPrenom("SuperUtilisateur");
                admin.setRoles(new HashSet<>());
                admin.getRoles().add(adminRole.get());

                userRepository.save(admin);
                System.out.println("✅ Utilisateur admin ajouté : admin / admin123");
            }
        }
    }
}

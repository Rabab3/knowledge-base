package com.example.knowledgebase.config;

import com.example.knowledgebase.model.ERole;
import com.example.knowledgebase.model.Role;
import com.example.knowledgebase.repository.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.boot.context.event.ApplicationReadyEvent;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final RoleRepository roleRepository;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initializeRoles() {
         //TEMPORAIRE : commenter pour test
         for (ERole roleName : ERole.values()) {
             if (roleRepository.findByName(roleName).isEmpty()) {
                 roleRepository.save(new Role(null, roleName));
                 System.out.println("Inserted role: " + roleName);
             }}
     }
}

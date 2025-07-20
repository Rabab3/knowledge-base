package com.example.knowledgebase.mapper;

import com.example.knowledgebase.dto.UserDto;
import com.example.knowledgebase.model.Role;
import com.example.knowledgebase.model.User;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public User toEntity(UserDto dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setNom(dto.getNom());
        user.setPrenom(dto.getPrenom());
        user.setEmail(dto.getEmail());
        user.setCin(dto.getCin());
        user.setTelephone(dto.getTelephone());
        user.setDateNaissance(dto.getDateNaissance());
        return user;
    }

    // ✅ Méthode ajoutée pour permettre le mappage avec rôles et mot de passe hashé
    public User toEntity(UserDto dto, Set<Role> roles, String encodedPassword) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setNom(dto.getNom());
        user.setPrenom(dto.getPrenom());
        user.setEmail(dto.getEmail());
        user.setCin(dto.getCin());
        user.setTelephone(dto.getTelephone());
        user.setDateNaissance(dto.getDateNaissance());
        user.setRoles(roles); // Injection des rôles
        user.setPassword(encodedPassword); // Mot de passe déjà hashé
        return user;
    }

    public UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setNom(user.getNom());
        dto.setPrenom(user.getPrenom());
        dto.setEmail(user.getEmail());
        dto.setCin(user.getCin());
        dto.setTelephone(user.getTelephone());
        dto.setDateNaissance(user.getDateNaissance());
        dto.setRoles(user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet()));
        return dto;
    }
}
    
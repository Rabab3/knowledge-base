package com.example.knowledgebase.mapper;

import com.example.knowledgebase.dto.UserDto;
import com.example.knowledgebase.model.User;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserMapper {

    public User toEntity(UserDto dto) {
        User user = new User();
        user.setEmail(dto.getEmail());
        // Les autres champs comme nom, prénom, mot de passe doivent venir d’un autre DTO si besoin
        return user;
    }
    public UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setRoles(user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet()));
        return dto;
    }
}


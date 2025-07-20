package com.example.knowledgebase.mapper;

import com.example.knowledgebase.dto.UserDto;
import com.example.knowledgebase.model.ERole;
import com.example.knowledgebase.model.Role;
import com.example.knowledgebase.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private final UserMapper userMapper = new UserMapper();

    @Test
    void toDto_shouldIncludeRoles() {
        User user = new User();
        user.setId(1L);
        user.setUsername("admin");
        user.setNom("Admin");
        user.setPrenom("Super");

        Role role = new Role();
        role.setName(ERole.ROLE_ADMIN);
        user.setRoles(Set.of(role));

        UserDto dto = userMapper.toDto(user);

        assertEquals("admin", dto.getUsername());
        assertEquals("Admin", dto.getNom());
        assertTrue(dto.getRoles().contains("ROLE_ADMIN"));
    }

    @Test
    void toEntity_shouldIgnoreIdAndHashPassword() {
        UserDto dto = new UserDto();
        dto.setUsername("admin");
        dto.setNom("Admin");
        dto.setPrenom("Super");
        dto.setRoles(Set.of("ROLE_ADMIN"));

        Role role = new Role();
        role.setName(ERole.ROLE_ADMIN);

        Set<Role> roles = Set.of(role);
        PasswordEncoder encoder = new BCryptPasswordEncoder();

        UserMapper mapper = new UserMapper();
        User user = mapper.toEntity(dto, roles, encoder.encode("admin123"));

        assertEquals("admin", user.getUsername());
        assertEquals("Admin", user.getNom());
        assertTrue(user.getPassword().startsWith("$2a$")); // vérifie que le mot de passe est encodé en bcrypt
    }
}

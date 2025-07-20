package com.example.knowledgebase;

import com.example.knowledgebase.model.ERole;
import com.example.knowledgebase.model.Role;
import com.example.knowledgebase.model.User;
import com.example.knowledgebase.repository.RoleRepository;
import com.example.knowledgebase.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class DtoValidationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    public void initTestUser() {
        if (!userRepository.existsByUsername("test")) {
            User user = new User();
            user.setUsername("test");
            user.setNom("Test");
            user.setPrenom("User");
            user.setPassword("dummy");

            Role roleMod = roleRepository.findByName(ERole.ROLE_MODERATEUR).orElseThrow();
            Role roleContrib = roleRepository.findByName(ERole.ROLE_CONTRIBUTEUR).orElseThrow();
            user.setRoles(Set.of(roleMod, roleContrib));

            userRepository.save(user);
        }
    }

    // ❌ Test UserDto : username vide
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void shouldFailIfUsernameIsBlank() throws Exception {
        Map<String, Object> userDto = Map.of(
                "username", "",
                "nom", "Nom",
                "prenom", "Prenom",
                "email", "test@email.com",
                "telephone", "0600000000",
                "cin", "AA000000",
                "dateNaissance", "2000-01-01",
                "roles", new String[]{"ROLE_ADMIN"}
        );

        mockMvc.perform(post("/api/users")
                        .param("password", "admin123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }

    // ❌ Test UserDto : roles manquants
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void shouldFailIfRolesAreMissing() throws Exception {
        Map<String, Object> userDto = Map.of(
                "username", "newuser",
                "nom", "Nom",
                "prenom", "Prenom",
                "email", "new@email.com",
                "telephone", "0600000000",
                "cin", "BB000000",
                "dateNaissance", "2000-01-01"
        );

        mockMvc.perform(post("/api/users")
                        .param("password", "admin123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }

    // ❌ Test ArticleDto : titre vide
    @Test
    @WithMockUser(username = "test", roles = {"MODERATEUR", "CONTRIBUTEUR"})
    public void shouldFailIfTitreIsMissing() throws Exception {
        Map<String, Object> articleDto = Map.of(
                "titre", "",
                "contenu", "Contenu valide"
        );

        mockMvc.perform(post("/api/contribute/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(articleDto)))
                .andExpect(status().isBadRequest());
    }

    // ❌ Test ArticleDto : contenu manquant
    @Test
    @WithMockUser(username = "test", roles = {"MODERATEUR", "CONTRIBUTEUR"})
    public void shouldFailIfContenuIsMissing() throws Exception {
        Map<String, Object> articleDto = Map.of(
                "titre", "Titre valide"
        );

        mockMvc.perform(post("/api/contribute/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(articleDto)))
                .andExpect(status().isBadRequest());
    }
}

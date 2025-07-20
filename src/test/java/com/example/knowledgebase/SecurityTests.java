package com.example.knowledgebase;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityTests {

    @Autowired
    private MockMvc mockMvc;

    // 🔒 1. Refuser l'accès à /api/users sans token
    @Test
    public void shouldDenyAccessToUsersEndpointWithoutToken() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isForbidden()); // ou .isUnauthorized() selon ta config
    }

    // ✅ 2. Autoriser /api/users uniquement aux ADMIN
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void shouldAllowAdminAccessToUsersEndpoint() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "lecteur", roles = {"LECTEUR"})
    public void shouldDenyNonAdminAccessToUsersEndpoint() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isForbidden());
    }

    // 🚫 3. Interdire l'accès aux articles "en attente" aux non-MODÉRATEUR
    @Test
    @WithMockUser(username = "contributeur", roles = {"CONTRIBUTEUR"})
    public void shouldDenyAccessToValiderIfNotModerateur() throws Exception {
        mockMvc.perform(get("/api/moderation/articles")
                        .param("statut", "EN_ATTENTE")) // ✅ valeur exacte de ton enum
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "moderateur", roles = {"MODERATEUR"})
    public void shouldAllowModerateurAccessToValider() throws Exception {
        mockMvc.perform(get("/api/moderation/articles")
                        .param("statut", "EN_ATTENTE")) // ✅ valeur exacte de ton enum
                .andExpect(status().isOk());
    }
}

package com.example.knowledgebase.controller;

import com.example.knowledgebase.model.Commentaire;
import com.example.knowledgebase.service.CommentaireService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class CommentaireController {

    private final CommentaireService commentaireService;
    @PostMapping("/{id}/commentaires")
    public Commentaire ajouter(@PathVariable Long id,
                               @RequestBody Map<String, String> body,
                               Authentication auth) {
        String content = body.get("content");

        //  TEST sans token (simulation)
        String email;
        if (auth == null) {
            email = "contributeur@email.com"; // utilisateur test
        } else {
            email = auth.getName();
        }

        return commentaireService.ajouterCommentaire(id, email, content);
    }
}


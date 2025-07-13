package com.example.knowledgebase.controller;

import com.example.knowledgebase.dto.ArticleDto;
import com.example.knowledgebase.model.ArticleStatus;
import com.example.knowledgebase.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/moderation/articles")
@RequiredArgsConstructor
public class ModerationController {

    private final ArticleService articleService;

    @PutMapping("/{id}/valider")
    @PreAuthorize("hasRole('MODERATEUR')")
    public ResponseEntity<ArticleDto> validerArticle(@PathVariable Long id) {
        return ResponseEntity.ok(articleService.validerAvecNotification(id));
    }

    @PutMapping("/{id}/retourner")
    @PreAuthorize("hasRole('MODERATEUR')")
    public ResponseEntity<ArticleDto> retournerArticle(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        String message = body.getOrDefault("message", "Article retourné pour correction.");
        return ResponseEntity.ok(articleService.retournerAvecCommentaire(id, message));
    }

    @GetMapping
    @PreAuthorize("hasRole('MODERATEUR')")
    public ResponseEntity<List<ArticleDto>> getArticlesByStatus(@RequestParam("statut") String statut) {
        return ResponseEntity.ok(articleService.getArticlesByStatus(statut));
    }
}

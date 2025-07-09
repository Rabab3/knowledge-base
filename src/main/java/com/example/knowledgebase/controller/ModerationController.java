package com.example.knowledgebase.controller;

import com.example.knowledgebase.dto.ArticleDto;
import com.example.knowledgebase.model.ArticleStatus;
import com.example.knowledgebase.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import com.example.knowledgebase.mapper.ArticleMapper;
import java.util.List;

@RestController
@RequestMapping("/api/moderation/articles")
@RequiredArgsConstructor
//@PreAuthorize("hasRole('MODERATEUR') or hasRole('ADMIN')")
public class ModerationController {

    private final ArticleService articleService;
    private final ArticleMapper articleMapper;

    @PutMapping("/{id}/valider")
    public ResponseEntity<ArticleDto> validerArticle(@PathVariable Long id) {
        return ResponseEntity.ok(articleService.validerAvecNotification(id));
    }

    @GetMapping
    public ResponseEntity<List<ArticleDto>> getArticlesByStatus(@RequestParam("statut") String statut) {
        return ResponseEntity.ok(articleService.getArticlesByStatus(statut));
    }

    @PutMapping("/{id}/retourner")
    public ResponseEntity<ArticleDto> retournerArticle(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        String message = body.getOrDefault("message", "Article retourné pour correction.");
        return ResponseEntity.ok(articleService.retournerAvecCommentaire(id, message));
    }



}

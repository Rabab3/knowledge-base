package com.example.knowledgebase.controller;

import com.example.knowledgebase.dto.ArticleDto;
import com.example.knowledgebase.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contribute/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @PostMapping
    public ResponseEntity<ArticleDto> create(@RequestBody ArticleDto dto, Authentication auth) {
        // Permet les tests sans sécurité : utilise un utilisateur par défaut si auth est null
        String username = (auth != null) ? auth.getName() : "test-user";
        System.out.println("Création d'article par : " + username);

        return ResponseEntity.ok(articleService.create(dto, username));
    }

    @GetMapping
    public ResponseEntity<List<ArticleDto>> getMine(Authentication auth) {
        String username = (auth != null) ? auth.getName() : "test-user";
        return ResponseEntity.ok(articleService.getByAuthor(username));
    }
}

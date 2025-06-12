package com.example.knowledgebase.controller;

import com.example.knowledgebase.model.Article;
import com.example.knowledgebase.service.FavoriService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class FavoriController {

    private final FavoriService favoriService;

    @PostMapping("/{id}/favoris")
    public void toggleFavori(@PathVariable Long id, Authentication auth) {
        String email = auth.getName();
        favoriService.toggleFavori(id, email);
    }

    @GetMapping("/favoris")
    public List<Article> getFavoris(Authentication auth) {
        String email = auth.getName();
        return favoriService.getFavoris(email);
    }
}

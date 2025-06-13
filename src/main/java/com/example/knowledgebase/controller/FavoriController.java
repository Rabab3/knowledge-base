package com.example.knowledgebase.controller;

import com.example.knowledgebase.model.Article;
import com.example.knowledgebase.model.User;
import com.example.knowledgebase.model.Favori;
import com.example.knowledgebase.repository.ArticleRepository;
import com.example.knowledgebase.repository.FavoriRepository;
import com.example.knowledgebase.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class FavoriController {

    private final FavoriRepository favoriRepository;
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;

    @PostMapping("/{id}/favoris")
    public ResponseEntity<?> toggleFavori(@PathVariable Long id, @RequestBody Map<String, Long> body) {
        Long userId = body.get("userId");
        if (userId == null) return ResponseEntity.badRequest().body("userId requis");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article introuvable"));

        //  Vérifie que l'article est VALIDE avant d'ajouter en favoris
        if (!"VALIDE".equalsIgnoreCase(article.getStatus().name())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Seuls les articles validés peuvent être ajoutés aux favoris");
        }

        Optional<Favori> existing = favoriRepository.findByUserAndArticle(user, article);
        if (existing.isPresent()) {
            favoriRepository.delete(existing.get());
            return ResponseEntity.ok("Favori retiré");
        } else {
            favoriRepository.save(new Favori(null, user, article));
            return ResponseEntity.ok("Favori ajouté");
        }
    }


    @GetMapping("/favoris/{userId}")
    public ResponseEntity<?> getFavoris(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        List<Article> favoris = favoriRepository.findByUser(user).stream().map(Favori::getArticle).toList();
        return ResponseEntity.ok(favoris);
    }
}

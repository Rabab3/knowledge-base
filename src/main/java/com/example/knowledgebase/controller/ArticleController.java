package com.example.knowledgebase.controller;

import com.example.knowledgebase.dto.ArticleDto;
import com.example.knowledgebase.dto.ArticleSearchRequest;
import com.example.knowledgebase.mapper.ArticleMapper;
import com.example.knowledgebase.model.Article;
import com.example.knowledgebase.model.ArticleVersion;
import com.example.knowledgebase.service.ArticleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contribute/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;
    private final ArticleMapper articleMapper;

    private String resolveUsername(Authentication auth) {
        if (auth == null || "anonymousUser".equals(auth.getName())) {
            return "contributeur@email.com";
        }
        return auth.getName();
    }

    @PostMapping
    public ResponseEntity<ArticleDto> create(@Valid @RequestBody ArticleDto dto, Authentication auth) {
        String username = resolveUsername(auth);
        return ResponseEntity.ok(articleService.create(dto, username));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id, Authentication auth) {
        String email = resolveUsername(auth);
        articleService.supprimerArticleSiValide(id, email);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/versions")
    public ResponseEntity<List<ArticleVersion>> getVersions(@PathVariable Long id) {
        return ResponseEntity.ok(articleService.getVersionsByArticleId(id));
    }

    @GetMapping
    public ResponseEntity<List<ArticleDto>> getMine(Authentication auth) {
        String username = resolveUsername(auth);
        List<ArticleDto> articles = articleService.getByAuthor(username);
        System.out.println("📥 Requête GET /contribute/articles par " + username + " → " + articles.size() + " articles");
        return ResponseEntity.ok(articles);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArticleDto> updateAsContributeur(@PathVariable Long id, @RequestBody ArticleDto dto, Authentication auth) {
        String username = resolveUsername(auth);
        ArticleDto updated = articleService.updateByContributeur(id, dto, username);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/drafts")
    public ResponseEntity<List<ArticleDto>> getDrafts(Authentication auth) {
        String username = resolveUsername(auth);
        return ResponseEntity.ok(articleService.getDraftsByAuthor(username));
    }

    @PostMapping("/search")
    public ResponseEntity<Page<ArticleDto>> searchArticles(
            @RequestBody ArticleSearchRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort
    ) {
        Sort.Order order = sort[1].equalsIgnoreCase("desc") ?
                Sort.Order.desc(sort[0]) : Sort.Order.asc(sort[0]);
        Pageable pageable = PageRequest.of(page, size, Sort.by(order));

        Page<Article> resultPage = articleService.searchArticles(request, pageable);
        Page<ArticleDto> dtoPage = resultPage.map(articleMapper::toDto);
        return ResponseEntity.ok(dtoPage);
    }



    // ✅ Nouveau endpoint pour récupérer un article par son ID si c'est le sien
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CONTRIBUTEUR') or hasRole('ADMIN')")
    public ResponseEntity<ArticleDto> getById(@PathVariable Long id, Authentication auth) {
        String username = resolveUsername(auth);
        ArticleDto dto = articleService.getByIdIfAuthor(id, username);
        return ResponseEntity.ok(dto);
    }
}

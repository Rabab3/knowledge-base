package com.example.knowledgebase.controller;

import com.example.knowledgebase.dto.ArticleDto;
import com.example.knowledgebase.dto.ArticleSearchRequest;
import com.example.knowledgebase.model.Article;
import com.example.knowledgebase.model.ArticleStatus;
import com.example.knowledgebase.model.ArticleVersion;
import com.example.knowledgebase.service.ArticleService;
import com.example.knowledgebase.mapper.ArticleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/contribute/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;
    private final ArticleMapper articleMapper;

    @PostMapping
    public ResponseEntity<ArticleDto> create(@RequestBody ArticleDto dto, Authentication auth) {
        String username = (auth != null) ? auth.getName() : "test-user";
        return ResponseEntity.ok(articleService.create(dto, username));
    }

    @GetMapping("/{id}/versions")
    public ResponseEntity<List<ArticleVersion>> getVersions(@PathVariable Long id) {
        return ResponseEntity.ok(articleService.getVersionsByArticleId(id)); // utilise le service
    }

    @GetMapping
    public ResponseEntity<List<ArticleDto>> getMine(Authentication auth) {
        String username = (auth != null) ? auth.getName() : "test-user";
        return ResponseEntity.ok(articleService.getByAuthor(username));
    }

    @GetMapping("/drafts")
    public ResponseEntity<List<ArticleDto>> getDrafts(Authentication auth) {
        String username = (auth != null) ? auth.getName() : "test-user";
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
}


package com.example.knowledgebase.service;

import com.example.knowledgebase.dto.ArticleDto;
import com.example.knowledgebase.dto.ArticleSearchRequest;
import com.example.knowledgebase.mapper.ArticleMapper;
import com.example.knowledgebase.model.*;
import com.example.knowledgebase.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final ArticleMapper articleMapper;
    private final NotificationService notificationService;
    private final ArticleVersionRepository versionRepository;

    /**
     * Crée un nouvel article.
     */
    public ArticleDto create(ArticleDto dto, String username) {
        User author = getOrFakeUser(username);

        Article article = articleMapper.toEntity(dto, author);
        article.setCreationDate(LocalDateTime.now());
        article.setModificationDate(null);

        if (dto.getStatus() != null) {
            article.setStatus(ArticleStatus.valueOf(dto.getStatus()));
        } else {
            article.setStatus(ArticleStatus.EN_ATTENTE);
        }

        article.setDraft(article.getStatus() == ArticleStatus.BROUILLON);

        return articleMapper.toDto(articleRepository.save(article));
    }

    public ArticleDto modifierArticle(Long articleId, ArticleDto updatedDto) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article non trouvé"));

        ArticleVersion version = ArticleVersion.builder()
                .titre(article.getTitle())
                .contenu(article.getContent())
                .status(article.getStatus())
                .sauvegardeLe(LocalDateTime.now())
                .article(article)
                .auteur(article.getAuthor())
                .build();
        versionRepository.save(version);

        article.setTitle(updatedDto.getTitle());
        article.setContent(updatedDto.getContent());
        article.setModificationDate(LocalDateTime.now());

        return articleMapper.toDto(articleRepository.save(article));
    }

    public List<ArticleDto> getByAuthor(String username) {
        User author = getOrFakeUser(username);
        List<ArticleDto> articles = articleRepository.findByAuthor(author).stream()
                .map(articleMapper::toDto)
                .toList();
        System.out.println("🔍 getByAuthor : " + username + " → " + articles.size() + " article(s)");
        return articles;
    }

    public List<ArticleDto> getDraftsByAuthor(String username) {
        User author = getOrFakeUser(username);
        return articleRepository.findByAuthorAndIsDraftTrue(author).stream()
                .map(articleMapper::toDto)
                .toList();
    }

    public Page<Article> searchArticles(ArticleSearchRequest request, Pageable pageable) {
        Specification<Article> spec = Specification
                .where(ArticleSpecification.hasTitre(request.getTitre()))
                .and(ArticleSpecification.hasContenu(request.getContenu()))
                .and(ArticleSpecification.hasAuteur(request.getAuteurId()))
                .and(ArticleSpecification.hasStatut(request.getStatus()))
                .and(ArticleSpecification.hasAuteurNom(request.getAuteurNom()))
                .and(ArticleSpecification.hasAuteurPrenom(request.getAuteurPrenom()))
                .and(ArticleSpecification.createdAfter(request.getCreatedAfter()))
                .and(ArticleSpecification.createdBefore(request.getCreatedBefore()));

        return articleRepository.findAll(spec, pageable);
    }

    public void supprimerArticleSiValide(Long articleId, String username) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article non trouvé"));

        if (article.getStatus() != ArticleStatus.VALIDE) {
            throw new RuntimeException("Seuls les articles validés peuvent être supprimés.");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        boolean isModerateur = user.getRoles().stream()
                .anyMatch(role -> role.getName().name().equals("ROLE_MODERATEUR"));

        if (!isModerateur) {
            throw new RuntimeException("Seul un modérateur peut supprimer un article validé.");
        }

        articleRepository.delete(article);
    }

    public ArticleDto retournerAvecCommentaire(Long id, String message) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article non trouvé"));

        article.setStatus(ArticleStatus.A_CORRIGER);
        article.setModificationDate(LocalDateTime.now());
        articleRepository.save(article);

        notificationService.notifier(article.getAuthor(), "✏️ Article retourné : " + message, article);

        return articleMapper.toDto(article);
    }

    public ArticleDto validerAvecNotification(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article non trouvé"));

        article.setStatus(ArticleStatus.VALIDE);
        article.setModificationDate(LocalDateTime.now());

        notificationService.notifier(
                article.getAuthor(),
                "✅ Votre article a été validé : " + article.getTitle(),
                article
        );

        return articleMapper.toDto(articleRepository.save(article));
    }

    private User getOrFakeUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé : " + username));
    }

    public List<ArticleVersion> getVersionsByArticleId(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article non trouvé"));
        return versionRepository.findByArticleOrderBySauvegardeLeDesc(article);
    }

    public List<ArticleDto> getByStatus(ArticleStatus status) {
        return articleRepository.findByStatus(status)
                .stream()
                .map(articleMapper::toDto)
                .toList();
    }

    public List<ArticleDto> getArticlesByStatus(String status) {
        ArticleStatus statut;
        try {
            statut = ArticleStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Statut invalide : " + status);
        }

        return articleRepository.findByStatus(statut)
                .stream()
                .map(articleMapper::toDto)
                .toList();
    }
}

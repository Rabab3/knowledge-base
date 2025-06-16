package com.example.knowledgebase.service;

import com.example.knowledgebase.dto.ArticleDto;
import com.example.knowledgebase.dto.ArticleSearchRequest;
import com.example.knowledgebase.mapper.ArticleMapper;
import com.example.knowledgebase.model.Article;
import com.example.knowledgebase.model.ArticleStatus;
import com.example.knowledgebase.model.ArticleVersion;
import com.example.knowledgebase.model.User;
import com.example.knowledgebase.repository.ArticleRepository;
import com.example.knowledgebase.repository.ArticleSpecification;
import com.example.knowledgebase.repository.ArticleVersionRepository;
import com.example.knowledgebase.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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

    /**
     * Crée un nouvel article.
     */
    public ArticleDto create(ArticleDto dto, String email) {
        User author = getOrFakeUser(email);

        Article article = articleMapper.toEntity(dto, author);
        article.setCreationDate(LocalDateTime.now());
        article.setModificationDate(null);
        article.setStatus(ArticleStatus.EN_ATTENTE);

        return articleMapper.toDto(articleRepository.save(article));
    }

    private final ArticleVersionRepository versionRepository;

    public ArticleDto modifierArticle(Long articleId, ArticleDto updatedDto) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article non trouvé"));

        // 👉 Sauvegarde de la version actuelle
        ArticleVersion version = ArticleVersion.builder()
                .titre(article.getTitle())
                .contenu(article.getContent())
                .status(article.getStatus())
                .sauvegardeLe(LocalDateTime.now())
                .article(article)
                .auteur(article.getAuthor())
                .build();
        versionRepository.save(version);

        // ✏️ Mise à jour de l'article
        article.setTitle(updatedDto.getTitle());
        article.setContent(updatedDto.getContent());
        article.setModificationDate(LocalDateTime.now());

        return articleMapper.toDto(articleRepository.save(article));
    }


    /**
     * Retourne les articles de l’auteur.
     */
    public List<ArticleDto> getByAuthor(String email) {
        User author = getOrFakeUser(email);

        return articleRepository.findByAuthor(author).stream()
                .map(articleMapper::toDto)
                .toList();
    }

    /**
     * Retourne les brouillons d’un auteur.
     */
    public List<ArticleDto> getDraftsByAuthor(String email) {
        User author = getOrFakeUser(email);

        return articleRepository.findByAuthorAndIsDraftTrue(author).stream()
                .map(articleMapper::toDto)
                .toList();
    }

    /**
     * Effectue une recherche avancée avec pagination et filtres.
     */
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
    public void supprimerArticleSiValide(Long articleId, String emailUtilisateur) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article non trouvé"));

        if (article.getStatus() != ArticleStatus.VALIDE) {
            throw new RuntimeException("Seuls les articles validés peuvent être supprimés.");
        }

        User user = userRepository.findByEmail(emailUtilisateur)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        boolean isModerateur = user.getRoles().stream()
                .anyMatch(role -> role.getName().name().equals("ROLE_MODERATEUR"));

        if (!isModerateur) {
            throw new RuntimeException("Seul un modérateur peut supprimer un article validé.");
        }

        articleRepository.delete(article);
    }



    /**
     * Marque un article comme retourné pour correction.
     */
    public ArticleDto retournerAvecCommentaire(Long id, String message) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article non trouvé"));

        article.setStatus(ArticleStatus.A_CORRIGER);
        article.setModificationDate(LocalDateTime.now());
        articleRepository.save(article);

        // 🔔 Notification à l’auteur
        notificationService.notifier(article.getAuthor(), "✏️ Article retourné : " + message, article);

        return articleMapper.toDto(article);
    }

    /**
     * Marque un article comme validé.
     */
    public ArticleDto validerAvecNotification(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article non trouvé"));

        article.setStatus(ArticleStatus.VALIDE);
        article.setModificationDate(LocalDateTime.now());

        // 🔔 Notification à l’auteur
        notificationService.notifier(
                article.getAuthor(),
                "✅ Votre article a été validé : " + article.getTitle(),
                article
        );

        return articleMapper.toDto(articleRepository.save(article));
    }

    /**
     * Utilise un utilisateur de test si nécessaire (sans auth réelle).
     */
    private User getOrFakeUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé : " + email));
    }
    public List<ArticleVersion> getVersionsByArticleId(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article non trouvé"));
        return versionRepository.findByArticleOrderBySauvegardeLeDesc(article);

    }


}

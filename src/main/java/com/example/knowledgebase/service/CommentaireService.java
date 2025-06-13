package com.example.knowledgebase.service;

import com.example.knowledgebase.model.Article;
import com.example.knowledgebase.model.Commentaire;
import com.example.knowledgebase.model.User;
import com.example.knowledgebase.repository.ArticleRepository;
import com.example.knowledgebase.repository.CommentaireRepository;
import com.example.knowledgebase.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentaireService {

    private final CommentaireRepository commentaireRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public Commentaire ajouterCommentaire(Long articleId, String email, String content) {
        User auteur = userRepository.findByEmail(email).orElseThrow();
        Article article = articleRepository.findById(articleId).orElseThrow();

        Commentaire commentaire = new Commentaire();
        commentaire.setAuthor(auteur);
        commentaire.setArticle(article);
        commentaire.setContent(content);
        commentaire.setDate(LocalDateTime.now());

        // ✅ Notification si ce n’est pas l’auteur
        if (!article.getAuthor().getEmail().equals(email)) {
            String message = "💬 Nouveau commentaire sur votre article : " + article.getTitle();
            notificationService.notifier(article.getAuthor(), message, article);
        }

        return commentaireRepository.save(commentaire);
    }
}


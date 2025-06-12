package com.example.knowledgebase.service;

import com.example.knowledgebase.model.*;
import com.example.knowledgebase.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriService {

    private final FavoriRepository favoriRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    public void toggleFavori(Long articleId, String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElseThrow();
        Article article = articleRepository.findById(articleId).orElseThrow();

        favoriRepository.findByUserAndArticle(user, article).ifPresentOrElse(
                favoriRepository::delete,
                () -> favoriRepository.save(new Favori(null, user, article))
        );
    }

    public List<Article> getFavoris(String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElseThrow();
        return favoriRepository.findByUser(user).stream()
                .map(Favori::getArticle)
                .toList();
    }
}

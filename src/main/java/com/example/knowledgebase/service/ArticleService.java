package com.example.knowledgebase.service;

import com.example.knowledgebase.dto.ArticleDto;
import com.example.knowledgebase.mapper.ArticleMapper;
import com.example.knowledgebase.model.Article;
import com.example.knowledgebase.model.ArticleStatus;
import com.example.knowledgebase.model.User;
import com.example.knowledgebase.repository.ArticleRepository;
import com.example.knowledgebase.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final ArticleMapper articleMapper;

    public ArticleDto create(ArticleDto dto, String email) {
        User author = getOrFakeUser(email); // ou sans paramètre

        Article article = articleMapper.toEntity(dto, author);
        article.setCreationDate(LocalDateTime.now());
        article.setModificationDate(null);
        article.setStatus(ArticleStatus.EN_ATTENTE);

        return articleMapper.toDto(articleRepository.save(article));
    }


    public List<ArticleDto> getByAuthor(String email) {
        User author = getOrFakeUser(email);

        return articleRepository.findByAuthor(author).stream()
                .map(articleMapper::toDto)
                .toList();
    }

    private User getOrFakeUser(String email) {
        // ❗ Utilise un utilisateur existant pour les tests sans token
        return userRepository.findByEmail("contributeur@email.com")
                .orElseThrow(() -> new RuntimeException("Utilisateur de test non trouvé"));
    }

}

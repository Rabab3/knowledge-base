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
        User author = getOrFakeUser(email);

        Article article = articleMapper.toEntity(dto, author);
        article.setCreationDate(LocalDateTime.now());
        article.setModificationDate(null);
        article.setStatus(ArticleStatus.EN_ATTENTE); // statut initial

        return articleMapper.toDto(articleRepository.save(article));
    }

    public List<ArticleDto> getByAuthor(String email) {
        User author = getOrFakeUser(email);

        return articleRepository.findByAuthor(author).stream()
                .map(articleMapper::toDto)
                .toList();
    }

    private User getOrFakeUser(String email) {
        return userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User fake = new User();
                    fake.setId(999L);
                    fake.setEmail(email);
                    fake.setNom("Test");
                    fake.setPrenom("User");
                    return fake;
                });
    }
}

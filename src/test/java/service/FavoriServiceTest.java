package service;

import com.example.knowledgebase.model.Article;
import com.example.knowledgebase.model.Favori;
import com.example.knowledgebase.model.User;
import com.example.knowledgebase.repository.ArticleRepository;
import com.example.knowledgebase.repository.FavoriRepository;
import com.example.knowledgebase.repository.UserRepository;
import com.example.knowledgebase.service.FavoriService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FavoriServiceTest {

    @InjectMocks
    private FavoriService favoriService;

    @Mock
    private FavoriRepository favoriRepository;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private UserRepository userRepository;

    private User user;
    private Article article;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setUsername("lecteur");

        article = new Article();
        article.setArticleId(1L);
    }

    @Test
    void toggleFavori_shouldAddIfNotExist_elseDelete() {
        when(userRepository.findByUsername("lecteur")).thenReturn(Optional.of(user));
        when(articleRepository.findById(1L)).thenReturn(Optional.of(article));
        when(favoriRepository.findByUserAndArticle(user, article)).thenReturn(Optional.empty());

        favoriService.toggleFavori(1L, "lecteur");

        verify(favoriRepository).save(any(Favori.class));

        // simulate existing favori
        when(favoriRepository.findByUserAndArticle(user, article)).thenReturn(Optional.of(new Favori(1L, user, article)));

        favoriService.toggleFavori(1L, "lecteur");

        verify(favoriRepository).delete(any(Favori.class));
    }

    @Test
    void getFavoris_shouldReturnArticlesFromFavoris() {
        Favori favori = new Favori(1L, user, article);

        when(userRepository.findByUsername("lecteur")).thenReturn(Optional.of(user));
        when(favoriRepository.findByUser(user)).thenReturn(List.of(favori));

        List<Article> result = favoriService.getFavoris("lecteur");

        assertEquals(1, result.size());
        assertEquals(article, result.get(0));
    }
}

package service;

import com.example.knowledgebase.dto.ArticleDto;
import com.example.knowledgebase.mapper.ArticleMapper;
import com.example.knowledgebase.model.*;
import com.example.knowledgebase.repository.ArticleRepository;
import com.example.knowledgebase.repository.ArticleVersionRepository;
import com.example.knowledgebase.repository.UserRepository;
import com.example.knowledgebase.service.ArticleService;
import com.example.knowledgebase.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ArticleServiceTest {

    @InjectMocks
    private ArticleService articleService;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ArticleMapper articleMapper;

    @Mock
    private NotificationService notificationService;

    @Mock
    private ArticleVersionRepository versionRepository;

    private User author;
    private Article article;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        author = new User();
        author.setId(1L);
        author.setUsername("author");
        author.setRoles(Set.of(new Role(1L, ERole.ROLE_CONTRIBUTEUR)));

        article = new Article();
        article.setArticleId(1L);
        article.setAuthor(author);
        article.setTitre("Old Title");
        article.setContenu("Old Content");
        article.setStatus(ArticleStatus.EN_ATTENTE);
    }

    @Test
    void create_shouldSaveArticle_withAuthor_andReturnDto() {
        ArticleDto dto = new ArticleDto();
        dto.setTitre("New Article");
        dto.setContenu("Content");

        when(userRepository.findByUsername("author")).thenReturn(Optional.of(author));
        when(articleMapper.toEntity(dto, author)).thenReturn(article);
        when(articleRepository.save(article)).thenReturn(article);
        when(articleMapper.toDto(article)).thenReturn(dto);

        ArticleDto result = articleService.create(dto, "author");

        assertNotNull(result);
        assertEquals("New Article", result.getTitre());
    }

    @Test
    void validerAvecNotification_shouldSetStatusValide_andNotify() {
        when(articleRepository.findById(1L)).thenReturn(Optional.of(article));
        when(articleRepository.save(article)).thenReturn(article);
        when(articleMapper.toDto(article)).thenReturn(new ArticleDto());

        ArticleDto result = articleService.validerAvecNotification(1L);

        assertNotNull(result);
        assertEquals(ArticleStatus.VALIDE, article.getStatus());
        verify(notificationService).notifier(eq(author), contains("✅"), eq(article));
    }

    @Test
    void retournerAvecCommentaire_shouldChangeStatus_andNotify() {
        when(articleRepository.findById(1L)).thenReturn(Optional.of(article));
        when(articleRepository.save(article)).thenReturn(article);
        when(articleMapper.toDto(article)).thenReturn(new ArticleDto());

        ArticleDto result = articleService.retournerAvecCommentaire(1L, "Corriger ceci");

        assertEquals(ArticleStatus.A_CORRIGER, article.getStatus());
        verify(notificationService).notifier(eq(author), contains("✏️"), eq(article));
    }
}

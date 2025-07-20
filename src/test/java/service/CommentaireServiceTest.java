package service;

import com.example.knowledgebase.model.Article;
import com.example.knowledgebase.model.Commentaire;
import com.example.knowledgebase.model.User;
import com.example.knowledgebase.repository.ArticleRepository;
import com.example.knowledgebase.repository.CommentaireRepository;
import com.example.knowledgebase.repository.UserRepository;
import com.example.knowledgebase.service.CommentaireService;
import com.example.knowledgebase.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommentaireServiceTest {

    @InjectMocks
    private CommentaireService commentaireService;

    @Mock
    private CommentaireRepository commentaireRepository;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    private User author;
    private Article article;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        author = new User();
        author.setUsername("auteur");

        article = new Article();
        article.setArticleId(1L);
        article.setAuthor(author);
        article.setTitre("Mon Article");
    }

    @Test
    void ajouterCommentaire_shouldSaveComment_andNotifyIfNotAuthor() {
        User commentAuthor = new User();
        commentAuthor.setUsername("lecteur");

        when(userRepository.findByUsername("lecteur")).thenReturn(Optional.of(commentAuthor));
        when(articleRepository.findById(1L)).thenReturn(Optional.of(article));

        Commentaire commentaire = new Commentaire();
        commentaire.setContent("Test commentaire");

        when(commentaireRepository.save(any(Commentaire.class))).thenReturn(commentaire);

        Commentaire result = commentaireService.ajouterCommentaire(1L, "lecteur", "Test commentaire");

        assertEquals("Test commentaire", result.getContent());
        verify(notificationService).notifier(eq(author), contains("💬"), eq(article));
    }
}

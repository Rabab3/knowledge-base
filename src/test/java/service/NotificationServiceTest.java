package service;

import com.example.knowledgebase.dto.NotificationDto;
import com.example.knowledgebase.model.Article;
import com.example.knowledgebase.model.Notification;
import com.example.knowledgebase.model.User;
import com.example.knowledgebase.repository.NotificationRepository;
import com.example.knowledgebase.service.NotificationEmitterService;
import com.example.knowledgebase.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    @InjectMocks
    private NotificationService notificationService;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationEmitterService emitterService;

    private User user;
    private Article article;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setUsername("admin");

        article = new Article();
        article.setArticleId(1L);
        article.setTitre("Test Article");
    }

    @Test
    void notifier_shouldSaveNotification_andEmitToUser() {
        String message = "Nouvelle notification";

        // WHEN
        notificationService.notifier(user, message, article);

        // THEN
        ArgumentCaptor<Notification> notifCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(notifCaptor.capture());

        Notification savedNotif = notifCaptor.getValue();
        assertEquals(user, savedNotif.getDestinataire());
        assertEquals(message, savedNotif.getMessage());
        assertEquals(article, savedNotif.getArticle());
        assertNotNull(savedNotif.getDate());

        // Vérifie envoi via SSE
        verify(emitterService).sendNotification(eq("admin"), any(NotificationDto.class));
    }

    @Test
    void getByUser_shouldReturnSortedList() {
        Notification n1 = new Notification();
        n1.setMessage("msg1");
        Notification n2 = new Notification();
        n2.setMessage("msg2");

        when(notificationRepository.findByDestinataireOrderByDateDesc(user)).thenReturn(List.of(n1, n2));

        List<Notification> result = notificationService.getByUser(user);

        assertEquals(2, result.size());
        verify(notificationRepository).findByDestinataireOrderByDateDesc(user);
    }
}

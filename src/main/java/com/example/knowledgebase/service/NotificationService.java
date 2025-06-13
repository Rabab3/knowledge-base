package com.example.knowledgebase.service;

import com.example.knowledgebase.dto.NotificationDto;
import com.example.knowledgebase.model.Article;
import com.example.knowledgebase.model.Notification;
import com.example.knowledgebase.model.User;
import com.example.knowledgebase.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationEmitterService emitterService;

    /**
     * Crée et enregistre une notification pour un utilisateur donné,
     * puis l'envoie en temps réel via SSE (Server-Sent Events).
     */
    public void notifier(User destinataire, String message, Article article) {
        // 💾 Enregistrement en base
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setDate(LocalDateTime.now());
        notification.setDestinataire(destinataire);
        notification.setArticle(article);
        notificationRepository.save(notification);

        // 🔁 Conversion pour l'envoi SSE
        NotificationDto dto = new NotificationDto(message, article);

        // 🔔 Envoi en temps réel via SSE
        emitterService.sendNotification(destinataire.getEmail(), dto);
    }

    /**
     * Récupère les notifications d’un utilisateur, triées par date décroissante.
     */
    public List<Notification> getByUser(User user) {
        return notificationRepository.findByDestinataireOrderByDateDesc(user);
    }
}

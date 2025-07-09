package com.example.knowledgebase.service;

import com.example.knowledgebase.dto.NotificationDto;
import com.example.knowledgebase.model.Article;
import com.example.knowledgebase.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class NotificationEmitterService {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter listen(String username) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.put(username, emitter);
        emitter.onCompletion(() -> emitters.remove(username));
        emitter.onTimeout(() -> emitters.remove(username));

        // ➕ Optionnel : envoyer un ping de bienvenue
        try {
            emitter.send(SseEmitter.event().name("connected").data("🔔 Connexion établie"));
        } catch (IOException e) {
            emitters.remove(username);
        }

        return emitter;
    }

    public void notifier(User destinataire, String message, Article article) {
        String username = destinataire.getUsername();
        SseEmitter emitter = emitters.get(username);
        if (emitter != null) {
            try {
                NotificationDto notif = new NotificationDto(message, article);
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(notif));
                log.info("✅ Notification envoyée à {}", username);
            } catch (IOException e) {
                emitters.remove(username);
                log.warn("❌ Erreur d'envoi de notification à {} : {}", username, e.getMessage());
            }
        } else {
            log.warn("⚠️ Aucun emitter trouvé pour {}", username);
        }
    }

    public void sendNotification(String username, NotificationDto dto) {
        SseEmitter emitter = emitters.get(username);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(dto));
            } catch (IOException e) {
                emitters.remove(username);
            }
        }
    }
}

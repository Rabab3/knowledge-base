package com.example.knowledgebase.controller;

import com.example.knowledgebase.model.Notification;
import com.example.knowledgebase.model.User;
import com.example.knowledgebase.repository.UserRepository;
import com.example.knowledgebase.service.NotificationService;
import com.example.knowledgebase.service.NotificationEmitterService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final NotificationEmitterService notificationEmitterService;

    @GetMapping
    public List<Notification> getMyNotifications(Authentication auth) {
        String email = (auth != null) ? auth.getName() : "contributeur@email.com";
        User user = userRepository.findByEmail(email).orElseThrow();
        return notificationService.getByUser(user);
    }

    @GetMapping("/stream")
    public SseEmitter stream(@RequestParam String email, HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("X-Accel-Buffering", "no"); // utile si tu testes derrière nginx
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:4200"); // OU "*" pour tests

        return notificationEmitterService.listen(email);
    }

}

package com.example.knowledgebase.repository;

import com.example.knowledgebase.model.Notification;
import com.example.knowledgebase.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByDestinataireOrderByDateDesc(User user);
}

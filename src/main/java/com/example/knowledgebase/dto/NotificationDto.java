package com.example.knowledgebase.dto;

import com.example.knowledgebase.model.Article;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotificationDto {
    private String message;
    private Article article;
}

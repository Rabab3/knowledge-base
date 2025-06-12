    package com.example.knowledgebase.dto;

    import lombok.Data;

    import java.time.LocalDateTime;

    @Data
    public class ArticleDto {
        private String status;
        private Long articleId;
        private String title;
        private String content;
        private LocalDateTime creationDate;
        private LocalDateTime modificationDate;
        private String authorEmail;
    }

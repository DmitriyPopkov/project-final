package com.example.LessonRest.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notices", schema = "distcomp")
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "newsId", nullable = false)
    private News news;

    // Конструкторы

    public Notice() {
    }

    public Notice(String content, News news) {
        this.content = content;
        this.news = news;

    }

    // Геттеры и сеттеры

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }



    public News getNews() {
        return news;
    }

    public void setNews(News news) {
        this.news = news;
    }
}

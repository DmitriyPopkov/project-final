package com.example.LessonRest.repository;

import com.example.LessonRest.entity.Notice;
import com.example.LessonRest.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
    Optional<Notice> findByNews(News news);
}
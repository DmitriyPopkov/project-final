package com.example.LessonRest.initializer;

import com.example.LessonRest.entity.News;
import com.example.LessonRest.entity.Editor;
import com.example.LessonRest.repository.EditorRepository;
import com.example.LessonRest.repository.NewsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Order(2)
@Component
public class NewsDataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(NewsDataInitializer.class);

    private final NewsRepository newsRepository;
    private final EditorRepository editorRepository;

    public NewsDataInitializer(NewsRepository newsRepository, EditorRepository editorRepository) {
        this.newsRepository = newsRepository;
        this.editorRepository = editorRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Проверяем, есть ли уже новости
        if (newsRepository.count() > 0) {
            logger.info("News already exist. Skipping initialization.");
            return;
        }

        // Проверяем, существует ли редактор с id=1
        if (editorRepository.findById(1L).isEmpty()) {
            logger.info("Editor with ID 1 not found. Skipping news initialization.");
            return;
        }

        Editor editor = editorRepository.findById(1L).get();
        LocalDateTime today = LocalDateTime.now();

        News news = new News();
        news.setTitle("Лето в деревне");
        news.setContent("Лето в деревне было замечательным!");
        news.setEditor(editor);
        news.setCreatedAt(today);
        news.setModifiedAt(today);

        newsRepository.save(news);
        logger.info("News with title 'Лето в деревне' created successfully!");
    }
}

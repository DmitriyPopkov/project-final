package com.example.LessonRest.initializer;

import com.example.LessonRest.entity.Notice;
import com.example.LessonRest.entity.News;
import com.example.LessonRest.repository.NewsRepository;
import com.example.LessonRest.repository.NoticeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;



@Component
@Transactional
public class NoticeDataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(NoticeDataInitializer.class);

    private final NoticeRepository noticeRepository;
    private final NewsRepository newsRepository;

    public NoticeDataInitializer(NoticeRepository noticeRepository, NewsRepository newsRepository) {
        this.noticeRepository = noticeRepository;
        this.newsRepository = newsRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Ищем первую новость
        News news = newsRepository.findAll().stream().findFirst().orElse(null);
        if (news == null) {
            logger.info("No news found. Skipping notice initialization.");
            return;
        }

        if (noticeRepository.findByNews(news).isEmpty()) {
            Notice notice = new Notice();
            notice.setContent("замечательно написано");
            notice.setNews(news);


            noticeRepository.save(notice);
            logger.info("Notice with content 'замечательно написано' created successfully!");
        } else {
            logger.info("Notice already exists for news.");
        }
    }
}

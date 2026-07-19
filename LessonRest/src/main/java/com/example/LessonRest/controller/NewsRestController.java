package com.example.LessonRest.controller;

import com.example.LessonRest.dto.NewsRequestTo;
import com.example.LessonRest.dto.NewsResponseTo;
import com.example.LessonRest.entity.News;
import com.example.LessonRest.repository.NewsRepository;
import com.example.LessonRest.repository.EditorRepository;
import com.example.LessonRest.mapper.NewsMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1.0/news")
public class NewsRestController {

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private NewsMapper newsMapper;

    @Autowired
    private EditorRepository editorRepository;

    @GetMapping
    public List<NewsResponseTo> getAllNews() {
        return newsRepository.findAll().stream()
                .map(newsMapper::toResponseTo)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public NewsResponseTo getNewsById(@PathVariable Long id) {
        return newsRepository.findById(id)
                .map(newsMapper::toResponseTo)
                .orElse(null);
    }

    @PostMapping
    public ResponseEntity<NewsResponseTo> createNews(@Valid @RequestBody NewsRequestTo request) {
        News news = newsMapper.toEntity(request);
        news.setCreatedAt(java.time.LocalDateTime.now());
        news.setModifiedAt(java.time.LocalDateTime.now());
        if (request.getEditorId() != null) {
            news.setEditor(editorRepository.findById(request.getEditorId()).orElse(null));
        }
        News saved = newsRepository.save(news);
        NewsResponseTo response = newsMapper.toResponseTo(saved);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NewsResponseTo> updateNews(@PathVariable Long id, @Valid @RequestBody NewsRequestTo request) {
        News news = newsRepository.findById(id).orElse(null);
        if (news != null) {
            news.setTitle(request.getTitle());
            news.setContent(request.getContent());
            news.setModifiedAt(java.time.LocalDateTime.now());
            if (request.getEditorId() != null) {
                news.setEditor(editorRepository.findById(request.getEditorId()).orElse(null));
            }
            News saved = newsRepository.save(news);
            return ResponseEntity.ok(newsMapper.toResponseTo(saved));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNews(@PathVariable Long id) {
        if (newsRepository.existsById(id)) {
            newsRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

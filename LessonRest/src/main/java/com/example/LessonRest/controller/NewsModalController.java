package com.example.LessonRest.controller;

import com.example.LessonRest.dto.EditorResponseTo;
import com.example.LessonRest.dto.NewsRequestTo;
import com.example.LessonRest.repository.EditorRepository;
import com.example.LessonRest.repository.NewsRepository;
import com.example.LessonRest.mapper.EditorMapper;
import com.example.LessonRest.mapper.NewsMapper;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/news")
public class NewsModalController {

    private static final Logger logger = LoggerFactory.getLogger(NewsModalController.class);

    @Autowired
    private EditorRepository editorRepository;

    @Autowired
    private EditorMapper editorMapper;

    @Autowired
    private NewsMapper newsMapper;

    @Autowired
    private NewsRepository newsRepository;

    @GetMapping("/modal/editors")
    public List<EditorResponseTo> getAllEditorsForModal() {
        return editorRepository.findAll().stream()
                .map(editorMapper::toResponseTo)
                .collect(Collectors.toList());
    }

    @PostMapping("/modal/create")
    public String createNewsFromModal(@Valid @ModelAttribute("newsForm") NewsRequestTo news,
                                      BindingResult result,
                                      Model model) {
        logger.info("NewsModalController.createNewsFromModal called - title={}, content={}, editorId={}",
                news.getTitle(), news.getContent(), news.getEditorId());
        
        if (result.hasErrors()) {
            logger.warn("Validation failed: {}", result.getAllErrors().get(0).getDefaultMessage());
            List<EditorResponseTo> editors = editorRepository.findAll().stream()
                    .map(editorMapper::toResponseTo)
                    .collect(Collectors.toList());
            model.addAttribute("editors", editors);
            model.addAttribute("error", "Validation failed: " + result.getAllErrors().get(0).getDefaultMessage());
            return "news/list";
        }

        try {
            logger.info("Converting to entity");
            var entity = newsMapper.toEntity(news);
            
            // Загружаем editor по ID и устанавливаем его в сущность
            var editor = editorRepository.findById(news.getEditorId()).orElse(null);
            if (editor == null) {
                String errorMsg = "Editor not found with ID: " + news.getEditorId();
                logger.error(errorMsg);
                throw new Exception(errorMsg);
            }
            entity.setEditor(editor);
            
            logger.info("Entity created: createdAt={}, editorId={}", entity.getCreatedAt(), entity.getEditor().getId());
            newsRepository.save(entity);
            logger.info("Entity saved with ID: {}", entity.getId());
            model.addAttribute("success", true);
            model.addAttribute("message", "News created successfully! ID: " + entity.getId());
        } catch (Exception e) {
            logger.error("Error saving news: {}", e.getMessage(), e);
            model.addAttribute("error", e.getMessage());
            List<EditorResponseTo> editors = editorRepository.findAll().stream()
                    .map(editorMapper::toResponseTo)
                    .collect(Collectors.toList());
            model.addAttribute("editors", editors);
            return "news/list";
        }

        return "redirect:/news";
    }
}

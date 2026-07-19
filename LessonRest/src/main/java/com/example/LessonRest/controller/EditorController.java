package com.example.LessonRest.controller;

import com.example.LessonRest.dto.EditorRequestTo;
import com.example.LessonRest.dto.EditorResponseTo;
import com.example.LessonRest.entity.User;
import com.example.LessonRest.repository.EditorRepository;
import com.example.LessonRest.repository.NewsRepository;
import com.example.LessonRest.repository.StickerRepository;
import com.example.LessonRest.repository.UserRepository;
import com.example.LessonRest.mapper.EditorMapper;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/editors")
public class EditorController {

    @Autowired
    private EditorRepository editorRepository;

    private static final Logger logger = LoggerFactory.getLogger(EditorController.class);

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private StickerRepository stickerRepository;

    @Autowired
    private EditorMapper editorMapper;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String listEditorsForMainPage(Model model, @RequestParam(value = "editorLogin", required = false) String editorLogin) {
        // Получаем текущего пользователя и его роль
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = null;
        
        if (authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser")) {
            currentUser = userRepository.findByUsernameWithRole(authentication.getName()).orElse(null);
        }
        
        if (currentUser != null) {
            model.addAttribute("currentUserRole", currentUser.getRoleName());
        }
        
        // Если передан editorLogin в параметрах запроса, фильтруем по нему
        if (editorLogin != null && !editorLogin.isEmpty()) {
            com.example.LessonRest.entity.Editor currentEditor = editorRepository.findByLogin(editorLogin).orElse(null);
            if (currentEditor != null) {
                List<com.example.LessonRest.entity.Editor> editors = new ArrayList<>();
                editors.add(currentEditor);
                model.addAttribute("editors", editors);
                // Заполняем форму данными только этого редактора
                model.addAttribute("editor", editorMapper.toRequestTo(editorMapper.toResponseTo(currentEditor)));
                return "editors/list";
            }
        }
        
        // Если пользователь - редактор (EDITOR роль), показываем только его данные
        if (currentUser != null && ("EDITOR".equals(currentUser.getRoleName()) || "ROLE_EDITOR".equals(currentUser.getRoleName()))) {
            // Фильтруем редакторов по текущему редактору
            com.example.LessonRest.entity.Editor currentEditor = editorRepository.findByLogin(currentUser.getUsername()).orElse(null);
            if (currentEditor != null) {
                List<com.example.LessonRest.entity.Editor> editors = new ArrayList<>();
                editors.add(currentEditor);
                // Заполняем форму данными только этого редактора
                model.addAttribute("editor", editorMapper.toRequestTo(editorMapper.toResponseTo(currentEditor)));
                return "editors/list";
            }
        }
        
        // Для ADMIN и других показываем всех редакторов
        List<EditorResponseTo> editors = editorRepository.findAll().stream()
                .map(editorMapper::toResponseTo)
                .collect(ArrayList::new, (list, item) -> list.add(item), List::addAll);
        model.addAttribute("editors", editors);
        model.addAttribute("editor", new EditorRequestTo());
        
        return "editors/list";
    }

    @GetMapping("/create")
    public String createEditorForm(Model model) {
        model.addAttribute("editor", new EditorRequestTo());
        model.addAttribute("editorId", null);
        return "editors/form";
    }

    @PostMapping("/create")
    public String createEditor(@Valid @ModelAttribute("editor") EditorRequestTo editor,
                               BindingResult result,
                               Model model) {
        if (result.hasErrors()) {
            return "editors/form";
        }

        try {
            var entity = editorMapper.toEntity(editor);
            var saved = editorRepository.save(entity);
            var response = editorMapper.toResponseTo(saved);
            model.addAttribute("success", true);
            model.addAttribute("message", "Editor created successfully! ID: " + response.getId());
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "editors/form";
        }

        return "redirect:/editors";
    }

    @GetMapping("/{id}/edit")
    public String editEditorForm(@PathVariable Long id, Model model) {
        var entity = editorRepository.findById(id).orElse(null);
        if (entity != null) {
            var response = editorMapper.toResponseTo(entity);
            model.addAttribute("editor", editorMapper.toRequestTo(response));
            model.addAttribute("editorId", id);
            
            // Получаем текущего пользователя и его роль
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = null;
            if (authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser")) {
                currentUser = userRepository.findByUsernameWithRole(authentication.getName()).orElse(null);
            }
            if (currentUser != null) {
                model.addAttribute("currentUserRole", currentUser.getRoleName());
            }
        }
        return "editors/form";
    }

    @PostMapping("/{id}/edit")
    public String updateEditor(@PathVariable Long id, @Valid @ModelAttribute("editor") EditorRequestTo editor,
                               BindingResult result,
                               Model model) {
        if (result.hasErrors()) {
            return "editors/form";
        }

        try {
            var entity = editorRepository.findById(id).orElse(null);
            if (entity != null) {
                entity.setLogin(editor.getLogin());
                entity.setFirstname(editor.getFirstname());
                entity.setLastname(editor.getLastname());
                editorRepository.save(entity);
                model.addAttribute("success", true);
                model.addAttribute("message", "Editor updated successfully!");
            } else {
                model.addAttribute("error", "Editor not found");
            }
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        return "redirect:/editors";
    }

    @PostMapping("/{id}/delete")
    public String deleteEditor(@PathVariable Long id, Model model) {
        try {
            var editor = editorRepository.findById(id).orElse(null);
            if (editor != null) {
                // Получаем все новости, связанные с этим редактором
                var newsList = newsRepository.findAll().stream()
                        .filter(news -> news.getEditor() != null && news.getEditor().getId().equals(id))
                        .toList();
                
                // Удаляем стикеры из всех новостей
                for (var news : newsList) {
                    if (news.getStickers() != null) {
                        news.getStickers().clear();
                        newsRepository.save(news);
                    }
                    
                    // Удаляем заметки из всех новостей (orphanRemoval сделает это автоматически)
                    news.getNotices().clear();
                    newsRepository.save(news);
                }
                
                // Удаляем сами новости
                for (var news : newsList) {
                    newsRepository.delete(news);
                }
                
                // Удаляем самого редактора
                editorRepository.delete(editor);
                
                model.addAttribute("success", true);
                model.addAttribute("message", "Editor deleted successfully!");
            } else {
                model.addAttribute("error", "Editor not found");
            }
        } catch (Exception e) {
            logger.error("Error deleting editor: {}", e.getMessage(), e);
            model.addAttribute("error", e.getMessage());
        }
        return "redirect:/editors";
    }
}

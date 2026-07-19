package com.example.LessonClient.controller;

import com.example.LessonClient.dto.NewsRequestTo;
import com.example.LessonClient.dto.NewsResponseTo;
import com.example.LessonClient.dto.StickerResponseTo;
import com.example.LessonClient.dto.ProfileResponseTo;
import com.example.LessonClient.service.ApiClient;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/news")
public class NewsController {

    @Autowired
    private ApiClient apiClient;

    @Autowired
    private WebClient webClient;

    @GetMapping
    public String listNews(Model model) {
        List<NewsResponseTo> newsList = apiClient.getAllNews().collectList().block();
        model.addAttribute("news", newsList != null ? newsList : new ArrayList<>());
        
        // Получаем текущего пользователя
        ProfileResponseTo profile = getCurrentProfile();
        if (profile != null) {
            model.addAttribute("currentUserRole", profile.getRole());
            model.addAttribute("currentUserUsername", profile.getUsername());
            
            // Если пользователь редактор, получаем его ID
            // Проверяем как с префиксом ROLE_, так и без
            String role = profile.getRole();
            boolean isEditor = (role != null) && (role.contains("EDITOR") || role.contains("ROLE_EDITOR"));
            if (isEditor) {
                Long editorId = getEditorIdByUsername(profile.getUsername());
                if (editorId != null) {
                    model.addAttribute("currentUserEditorId", editorId);
                }
            }
        }
        
        return "news/list";
    }

    @GetMapping("/{id}")
    public String getNewsById(@PathVariable Long id, Model model) {
        NewsResponseTo news = apiClient.getNewsById(id);
        model.addAttribute("news", news);
        
        // Load stickers
        List<StickerResponseTo> stickers = apiClient.getAllStickers().collectList().block();
        model.addAttribute("stickers", stickers != null ? stickers : new ArrayList<>());
        
        // Load notices
        model.addAttribute("notices", new ArrayList<>());
        
        // Получаем текущего редактора
        ProfileResponseTo profile = getCurrentProfile();
        String role = profile != null ? profile.getRole() : null;
        boolean isEditor = (role != null) && (role.contains("EDITOR") || role.contains("ROLE_EDITOR"));
        if (isEditor) {
            Long editorId = getEditorIdByUsername(profile.getUsername());
            if (editorId != null) {
                model.addAttribute("currentUserEditorId", editorId);
            }
        }
        
        return "news/detail";
    }

    @GetMapping("/create")
    public String createNewsForm(Model model) {
        model.addAttribute("news", new NewsRequestTo());
        
        // Получаем текущего редактора и устанавливаем его ID
        ProfileResponseTo profile = getCurrentProfile();
        String role = profile != null ? profile.getRole() : null;
        boolean isEditor = (role != null) && (role.contains("EDITOR") || role.contains("ROLE_EDITOR"));
        if (isEditor) {
            Long editorId = getEditorIdByUsername(profile.getUsername());
            if (editorId != null) {
                model.addAttribute("editorId", editorId);
                model.addAttribute("currentUserEditorId", editorId);
            } else {
                model.addAttribute("editorId", 1L); // fallback
            }
        } else {
            model.addAttribute("editorId", 1L);
        }
        
        model.addAttribute("stickerIds", new ArrayList<>());
        return "news/form";
    }

    @PostMapping("/create")
    public String createNews(@Valid @ModelAttribute("news") NewsRequestTo news,
                             BindingResult result,
                             Model model) {
        if (result.hasErrors()) {
            return "news/form";
        }

        try {
            NewsResponseTo created = apiClient.createNews(news);
            model.addAttribute("success", true);
            model.addAttribute("message", "News created successfully! ID: " + created.getId());
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "news/form";
        }

        return "redirect:/news";
    }

    @GetMapping("/{id}/edit")
    public String editNewsForm(@PathVariable Long id, Model model) {
        NewsResponseTo news = apiClient.getNewsById(id);
        model.addAttribute("news", news);
        model.addAttribute("editorId", news.getEditorId());
        model.addAttribute("stickerIds", news.getStickerIds() != null ? news.getStickerIds() : new ArrayList<>());
        
        // Получаем текущего редактора
        ProfileResponseTo profile = getCurrentProfile();
        String role = profile != null ? profile.getRole() : null;
        boolean isEditor = (role != null) && (role.contains("EDITOR") || role.contains("ROLE_EDITOR"));
        if (isEditor) {
            Long editorId = getEditorIdByUsername(profile.getUsername());
            if (editorId != null) {
                model.addAttribute("currentUserEditorId", editorId);
            }
        }
        
        return "news/form";
    }

    @PostMapping("/{id}/edit")
    public String updateNews(@PathVariable Long id, @Valid @ModelAttribute("news") NewsRequestTo news,
                             BindingResult result,
                             Model model) {
        if (result.hasErrors()) {
            return "news/form";
        }

        try {
            NewsResponseTo updated = apiClient.updateNews(id, news);
            model.addAttribute("success", true);
            model.addAttribute("message", "News updated successfully!");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "news/form";
        }

        return "redirect:/news";
    }

    @PostMapping("/{id}/delete")
    public String deleteNews(@PathVariable Long id, Model model) {
        try {
            apiClient.deleteNews(id);
            model.addAttribute("success", true);
            model.addAttribute("message", "News deleted successfully!");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "redirect:/news";
    }
    
    /**
     * Получает текущего аутентифицированного пользователя
     */
    private ProfileResponseTo getCurrentProfile() {
        try {
            return webClient.get()
                    .uri("/api/v1.0/auth/profile")
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), clientResponse -> Mono.empty())
                    .bodyToMono(ProfileResponseTo.class)
                    .block();
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Получает ID редактора по логину
     */
    private Long getEditorIdByUsername(String username) {
        try {
            // Используем Map вместо конкретного DTO, так как LessonClient не может импортировать DTO из LessonRest
            java.util.Map<String, Object> profile = webClient.get()
                    .uri("/api/v1.0/auth/editor-profile?login=" + username)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), clientResponse -> Mono.empty())
                    .bodyToMono(java.util.Map.class)
                    .block();
            if (profile != null && profile.containsKey("id")) {
                Object idValue = profile.get("id");
                if (idValue instanceof Integer) {
                    return ((Integer) idValue).longValue();
                } else if (idValue instanceof Long) {
                    return (Long) idValue;
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}

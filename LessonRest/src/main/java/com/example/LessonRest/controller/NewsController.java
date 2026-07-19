package com.example.LessonRest.controller;

import com.example.LessonRest.dto.EditorResponseTo;
import com.example.LessonRest.dto.NewsRequestTo;
import com.example.LessonRest.dto.NewsResponseTo;
import com.example.LessonRest.entity.Editor;
import com.example.LessonRest.entity.News;
import com.example.LessonRest.entity.User;
import com.example.LessonRest.repository.EditorRepository;
import com.example.LessonRest.repository.NewsRepository;
import com.example.LessonRest.repository.NoticeRepository;
import com.example.LessonRest.repository.StickerRepository;
import com.example.LessonRest.repository.UserRepository;
import com.example.LessonRest.mapper.EditorMapper;
import com.example.LessonRest.mapper.NewsMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/news")
public class NewsController {

    private static final Logger logger = LoggerFactory.getLogger(NewsController.class);

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private NewsMapper newsMapper;

    @Autowired
    private EditorRepository editorRepository;

    @Autowired
    private EditorMapper editorMapper;

    @Autowired
    private StickerRepository stickerRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String listNews(@RequestParam(required = false) Long editorId, @RequestParam(required = false) Long stickerId, Model model, HttpServletRequest request) {
        logger.info("NewsController.listNews called - editorId={}, stickerId={}", editorId, stickerId);
        
        // Получаем текущего пользователя
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = null;
        if (authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser")) {
            currentUser = userRepository.findByUsernameWithRole(authentication.getName()).orElse(null);
            logger.info("Current user: {}", currentUser != null ? currentUser.getUsername() : "null");
            if (currentUser != null && currentUser.getRole() != null) {
                logger.info("Current user role: {}", currentUser.getRole().getName());
            } else if (currentUser != null) {
                logger.info("Current user has no role assigned");
            }
        }
        
        // Определяем, какой editorId использовать для фильтрации
        // Приоритет фильтрации:
        // 1. Если editorId передан как параметр (пользователь выбрал редактора в UI)
        // 2. Если editorId есть в cookie (аутентификация редактора через API)
        // 3. Если пользователь аутентифицирован как редактор - фильтруем по его editorId
        Long finalEditorId = editorId;
        
        // Сначала проверяем cookie
        if (finalEditorId == null && request != null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("editorId".equals(cookie.getName())) {
                        try {
                            finalEditorId = Long.parseLong(cookie.getValue());
                            logger.info("Found editorId in cookie: {}", finalEditorId);
                            break;
                        } catch (NumberFormatException e) {
                            logger.warn("Invalid editorId in cookie: {}", cookie.getValue());
                        }
                    }
                }
            }
        }
        
        if (currentUser != null && finalEditorId == null) {
            String roleName = currentUser.getRoleName();
            logger.info("Role name from getRoleName(): {}", roleName);
            
            if (roleName != null && (roleName.contains("EDITOR") || roleName.contains("ROLE_EDITOR"))) {
                logger.info("User has EDITOR role - attempting to filter by editorId");
                
                // Пользователь аутентифицирован как редактор
                // Ищем его запись в tbl_editors по username (username == login)
                String editorLogin = currentUser.getUsername();
                logger.info("Searching for editor by login: {}", editorLogin);
                
                Editor currentEditor = editorRepository.findByLogin(editorLogin).orElse(null);
                if (currentEditor != null) {
                    // Автоматически фильтруем только его новости
                    finalEditorId = currentEditor.getId();
                    logger.info("Found editor with ID: {}. Filtering news for authenticated editor (role=EDITOR): {}", finalEditorId, editorLogin);
                } else {
                    logger.warn("Editor not found in tbl_editors for login: {}", editorLogin);
                }
            } else {
                logger.info("User does not have EDITOR role, role: {}", roleName);
            }
        } else {
            logger.info("Skipping filter - currentUser: {}, finalEditorId from param/cookie: {}", currentUser != null, finalEditorId);
        }
        
        // Сначала получаем все новости
        List<News> newsEntities = newsRepository.findAll();
        logger.info("Total news entities: {}", newsEntities.size());
        
        // Применяем фильтр по editorId, если он задан
        if (finalEditorId != null) {
            final Long filterId = finalEditorId;
            newsEntities = newsEntities.stream()
                    .filter(news -> news.getEditor() != null && news.getEditor().getId().equals(filterId))
                    .collect(Collectors.toList());
            logger.info("After editorId filter: {} news", newsEntities.size());
        }
        
        // Применяем фильтр по stickerId, если он задан (фильтруем уже найденные)
        if (stickerId != null) {
            final Long stickerIdFilter = stickerId;
            newsEntities = newsEntities.stream()
                    .filter(news -> news.getStickers() != null && news.getStickers().stream()
                            .anyMatch(sticker -> sticker.getId().equals(stickerIdFilter)))
                    .collect(Collectors.toList());
            logger.info("After stickerId filter: {} news", newsEntities.size());
        }
        
        List<NewsResponseTo> newsList = newsEntities.stream()
                .map(newsMapper::toResponseTo)
                .collect(ArrayList::new, (list, item) -> list.add(item), List::addAll);
        logger.info("Converted to {} NewsResponseTo", newsList.size());
        
        model.addAttribute("news", newsList);
        
        // Получаем список редакторов для модального окна создания новости
        List<EditorResponseTo> editors = editorRepository.findAll().stream()
                .map(editorMapper::toResponseTo)
                .collect(Collectors.toList());
        model.addAttribute("editors", editors);
        
        // Добавляем форму для создания новости
        model.addAttribute("newsForm", new NewsRequestTo());
        
        // Передаем ID фильтра для отображения в форме
        model.addAttribute("editorIdFilter", finalEditorId);
        model.addAttribute("stickerIdFilter", stickerId);
        
        // Передаем все стикеры для dropdown фильтра
        model.addAttribute("allStickers", stickerRepository.findAll());
        
        // Передаем роль текущего пользователя
        if (currentUser != null) {
            model.addAttribute("currentUserRole", currentUser.getRoleName());
            model.addAttribute("currentUserId", currentUser.getId());
            model.addAttribute("currentUser", currentUser);
        }
        
        // Передаем ID текущего редактора для проверки прав на редактирование
        // Для ADMIN и USER передаем null (они могут видеть все новости), для EDITOR - его ID из cookie/SecurityContext
        model.addAttribute("currentUserEditorId", finalEditorId);
        
        // Скрываем формы фильтрации только для EDITOR пользователей (они видят только свои новости)
        // ADMIN и USER видят все новости и могут фильтровать, поэтому hideFilters = false для них
        boolean hideFilters = (currentUser != null && finalEditorId != null && 
            currentUser.getRoleName() != null && 
            (currentUser.getRoleName().contains("EDITOR") || currentUser.getRoleName().contains("ROLE_EDITOR")));
        model.addAttribute("hideFilters", hideFilters);
        
        return "news/list";
    }

    @GetMapping("/{id}")
    public String getNewsById(@PathVariable Long id, Model model) {
        NewsResponseTo news = newsRepository.findById(id).map(newsMapper::toResponseTo).orElse(null);
        if (news != null) {
            model.addAttribute("news", news);
            
            // Получаем имя редактора по ID
            String editorName = null;
            if (news.getEditorId() != null) {
                var editor = editorRepository.findById(news.getEditorId()).orElse(null);
                if (editor != null) {
                    editorName = editor.getFirstname() + " " + editor.getLastname();
                }
            }
            model.addAttribute("editorName", editorName);
            
            // Получаем связанные Notice для этой новости
            var newsEntity = newsRepository.findById(id).orElse(null);
            if (newsEntity != null && newsEntity.getNotices() != null) {
                model.addAttribute("notices", newsEntity.getNotices());
            }
            
            // Получаем связанные Stickers для этой новости
            if (newsEntity != null && newsEntity.getStickers() != null) {
                model.addAttribute("stickers", newsEntity.getStickers());
            }
            
            // Получаем все стикеры для выбора
            model.addAttribute("allStickers", stickerRepository.findAll());
            
            // Получаем текущего пользователя и его роль
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = null;
            if (authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser")) {
                currentUser = userRepository.findByUsernameWithRole(authentication.getName()).orElse(null);
            }
            if (currentUser != null) {
                model.addAttribute("currentUserRole", currentUser.getRoleName());
            }
            
            // Передаем ID текущего редактора для проверки прав на редактирование
            // Используем тот же логический подход как в listNews - получаем editorId из cookie или SecurityContext
            Long currentUserEditorId = null;
            if (currentUser != null) {
                String roleName = currentUser.getRoleName();
                if (roleName != null && (roleName.contains("EDITOR") || roleName.contains("ROLE_EDITOR"))) {
                    String editorLogin = currentUser.getUsername();
                    Editor currentEditor = editorRepository.findByLogin(editorLogin).orElse(null);
                    if (currentEditor != null) {
                        currentUserEditorId = currentEditor.getId();
                    }
                }
            }
            model.addAttribute("currentUserEditorId", currentUserEditorId);
            
            return "news/detail";
        } else {
            model.addAttribute("error", "News not found");
            return "error";
        }
    }

    @GetMapping("/new")
    public String showNewForm(Model model) {
        model.addAttribute("news", new NewsRequestTo());
        return "news/form";
    }

    @PostMapping
    public String createNews(@Valid @ModelAttribute("news") NewsRequestTo news, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "news/form";
        }
        var entity = newsMapper.toEntity(news);
        newsRepository.save(entity);
        return "redirect:/news";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        NewsResponseTo news = newsRepository.findById(id).map(newsMapper::toResponseTo).orElse(null);
        if (news != null) {
            model.addAttribute("news", news);
            
            // Получаем имя редактора по ID
            String editorName = null;
            if (news.getEditorId() != null) {
                var editor = editorRepository.findById(news.getEditorId()).orElse(null);
                if (editor != null) {
                    editorName = editor.getFirstname() + " " + editor.getLastname();
                }
            }
            model.addAttribute("editorName", editorName);
            
            // Получаем текущего пользователя и его роль
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = null;
            if (authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser")) {
                currentUser = userRepository.findByUsernameWithRole(authentication.getName()).orElse(null);
            }
            if (currentUser != null) {
                model.addAttribute("currentUserRole", currentUser.getRoleName());
            }
            
            // Передаем ID текущего редактора для проверки прав на редактирование
            // Используем тот же логический подход как в listNews - получаем editorId из cookie или SecurityContext
            Long currentUserEditorId = null;
            if (currentUser != null) {
                String roleName = currentUser.getRoleName();
                if (roleName != null && (roleName.contains("EDITOR") || roleName.contains("ROLE_EDITOR"))) {
                    String editorLogin = currentUser.getUsername();
                    Editor currentEditor = editorRepository.findByLogin(editorLogin).orElse(null);
                    if (currentEditor != null) {
                        currentUserEditorId = currentEditor.getId();
                    }
                }
            }
            model.addAttribute("currentUserEditorId", currentUserEditorId);
            
            return "news/edit";
        } else {
            model.addAttribute("error", "News not found");
            return "error";
        }
    }

    @PostMapping("/{id}")
    public String updateNews(@PathVariable Long id, @RequestParam String title, @RequestParam String content) {
        var entity = newsRepository.findById(id).orElse(null);
        if (entity != null) {
            entity.setTitle(title);
            entity.setContent(content);
            entity.setModifiedAt(LocalDateTime.now());
            newsRepository.save(entity);
        }
        return "redirect:/news";
    }

    @PostMapping("/{id}/delete")
    public String deleteNews(@PathVariable Long id, Model model) {
        try {
            var news = newsRepository.findById(id).orElse(null);
            if (news != null) {
                // Удаляем все стикеры из этой новости
                if (news.getStickers() != null) {
                    news.getStickers().clear();
                    newsRepository.save(news);
                }
                
                // Удаляем все заметки, связанные с этой новостью (orphanRemoval сделает это автоматически)
                news.getNotices().clear();
                newsRepository.save(news);
                
                // Удаляем саму новость
                newsRepository.delete(news);
                
                model.addAttribute("success", true);
                model.addAttribute("message", "News deleted successfully!");
            } else {
                model.addAttribute("error", "News not found");
            }
        } catch (Exception e) {
            logger.error("Error deleting news: {}", e.getMessage(), e);
            model.addAttribute("error", e.getMessage());
        }
        return "redirect:/news";
    }
    
    @PostMapping("/{id}/stickers/{stickerId}")
    public String addStickerToNews(@PathVariable Long id, @PathVariable Long stickerId) {
        var news = newsRepository.findById(id).orElse(null);
        var sticker = stickerRepository.findById(stickerId).orElse(null);
        
        if (news != null && sticker != null) {
            news.getStickers().add(sticker);
            newsRepository.save(news);
        }
        return "redirect:/news/{id}";
    }
}

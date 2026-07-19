package com.example.LessonRest.controller;

import com.example.LessonRest.dto.NoticeRequestTo;
import com.example.LessonRest.dto.NoticeResponseTo;
import com.example.LessonRest.entity.News;
import com.example.LessonRest.entity.User;
import com.example.LessonRest.repository.NoticeRepository;
import com.example.LessonRest.repository.NewsRepository;
import com.example.LessonRest.repository.UserRepository;
import com.example.LessonRest.mapper.NoticeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/notices")
public class NoticeController {

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private NoticeMapper noticeMapper;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String listNotices(Model model) {
        List<NoticeResponseTo> notices = noticeRepository.findAll().stream()
                .map(noticeMapper::toResponseTo)
                .collect(ArrayList::new, (list, item) -> list.add(item), List::addAll);
        model.addAttribute("notices", notices);
        model.addAttribute("newsList", newsRepository.findAll());
        
        // Получаем текущего пользователя и его роль
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = null;
        if (authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser")) {
            currentUser = userRepository.findByUsernameWithRole(authentication.getName()).orElse(null);
        }
        if (currentUser != null) {
            model.addAttribute("currentUserRole", currentUser.getRoleName());
        }
        
        return "notices/list";
    }

    @GetMapping("/{id}")
    public String getNoticeById(@PathVariable Long id, Model model) {
        NoticeResponseTo notice = noticeRepository.findById(id).map(noticeMapper::toResponseTo).orElse(null);
        if (notice != null) {
            model.addAttribute("notice", notice);
            
            // Получаем текущего пользователя и его роль
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = null;
            if (authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser")) {
                currentUser = userRepository.findByUsernameWithRole(authentication.getName()).orElse(null);
            }
            if (currentUser != null) {
                model.addAttribute("currentUserRole", currentUser.getRoleName());
            }
            
            return "notices/detail";
        } else {
            model.addAttribute("error", "Notice not found");
            return "error";
        }
    }

    @GetMapping("/new")
    public String showNewForm(Model model) {
        model.addAttribute("notice", new NoticeRequestTo());
        model.addAttribute("newsList", newsRepository.findAll());
        return "notices/form";
    }

    @GetMapping("/create")
    public String showCreateForm(@RequestParam(required = false) Long newsId, Model model) {
        model.addAttribute("notice", new NoticeRequestTo());
        model.addAttribute("newsList", newsRepository.findAll());
        model.addAttribute("newsId", newsId);
        model.addAttribute("openModal", true);
        return "notices/list";
    }

    @PostMapping
    public String createNotice(@ModelAttribute("notice") NoticeRequestTo notice, @RequestParam Long newsId) {
        var entity = noticeMapper.toEntity(notice);
        News news = newsRepository.findById(newsId).orElse(null);
        if (news != null) {
            entity.setNews(news);
        }
        noticeRepository.save(entity);
        return "redirect:/notices";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        NoticeResponseTo notice = noticeRepository.findById(id).map(noticeMapper::toResponseTo).orElse(null);
        if (notice != null) {
            NoticeRequestTo request = noticeMapper.toRequestTo(notice);
            model.addAttribute("notice", request);
            model.addAttribute("newsList", newsRepository.findAll());
            return "notices/form";
        } else {
            model.addAttribute("error", "Notice not found");
            return "error";
        }
    }

    @PostMapping("/{id}")
    public String updateNoticeInline(@PathVariable Long id, @RequestParam String content, @RequestParam Long newsId) {
        var entity = noticeRepository.findById(id).orElse(null);
        if (entity != null) {
            entity.setContent(content);
            News news = newsRepository.findById(newsId).orElse(null);
            if (news != null) {
                entity.setNews(news);
            }
            noticeRepository.save(entity);
        }
        return "redirect:/notices";
    }

    @PostMapping("/{id}/delete")
    public String deleteNotice(@PathVariable Long id) {
        noticeRepository.deleteById(id);
        return "redirect:/notices";
    }
}

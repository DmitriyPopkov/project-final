package com.example.LessonClient.controller;

import com.example.LessonClient.dto.NoticeRequestTo;
import com.example.LessonClient.dto.NoticeResponseTo;
import com.example.LessonClient.service.ApiClient;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/notices")
public class NoticeController {

    @Autowired
    private ApiClient apiClient;

    @GetMapping
    public String listNotices(Model model) {
        List<NoticeResponseTo> notices = apiClient.getAllNotices().collectList().block();
        model.addAttribute("notices", notices != null ? notices : new ArrayList<>());
        return "notices/list";
    }

    @GetMapping("/{id}")
    public String getNoticeById(@PathVariable Long id, Model model) {
        NoticeResponseTo notice = apiClient.getNoticeById(id);
        model.addAttribute("notice", notice);
        return "notices/detail";
    }

    @GetMapping("/create")
    public String createNoticeForm(Model model) {
        model.addAttribute("notice", new NoticeRequestTo());
        return "notices/form";
    }

    @PostMapping("/create")
    public String createNotice(@Valid @ModelAttribute("notice") NoticeRequestTo notice,
                               BindingResult result,
                               Model model) {
        if (result.hasErrors()) {
            return "notices/form";
        }

        try {
            NoticeResponseTo created = apiClient.createNotice(notice);
            model.addAttribute("success", true);
            model.addAttribute("message", "Notice created successfully! ID: " + created.getId());
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "notices/form";
        }

        return "redirect:/notices";
    }

    @PostMapping("/{id}/edit")
    public String updateNotice(@PathVariable Long id, @Valid @ModelAttribute("notice") NoticeRequestTo notice,
                               BindingResult result,
                               Model model) {
        if (result.hasErrors()) {
            return "notices/form";
        }

        try {
            NoticeResponseTo updated = apiClient.updateNotice(id, notice);
            model.addAttribute("success", true);
            model.addAttribute("message", "Notice updated successfully!");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "notices/form";
        }

        return "redirect:/notices";
    }

    @PostMapping("/{id}/delete")
    public String deleteNotice(@PathVariable Long id, Model model) {
        try {
            apiClient.deleteNotice(id);
            model.addAttribute("success", true);
            model.addAttribute("message", "Notice deleted successfully!");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "redirect:/notices";
    }
}

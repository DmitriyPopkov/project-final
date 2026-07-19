package com.example.LessonClient.controller;

import com.example.LessonClient.dto.EditorRequestTo;
import com.example.LessonClient.dto.EditorResponseTo;
import com.example.LessonClient.service.ApiClient;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/editors")
public class EditorController {

    @Autowired
    private ApiClient apiClient;

    @GetMapping
    public String listEditors(Model model) {
        List<EditorResponseTo> editors = apiClient.getAllEditors().collectList().block();
        model.addAttribute("editors", editors != null ? editors : new ArrayList<>());
        return "editors/list";
    }

    @GetMapping("/create")
    public String createEditorForm(Model model) {
        model.addAttribute("editor", new EditorRequestTo());
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
            EditorResponseTo created = apiClient.createEditor(editor);
            model.addAttribute("success", true);
            model.addAttribute("message", "Editor created successfully! ID: " + created.getId());
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "editors/form";
        }

        return "redirect:/editors";
    }

    @GetMapping("/{id}/edit")
    public String editEditorForm(@PathVariable Long id, Model model) {
        EditorResponseTo editor = apiClient.getEditorById(id);
        model.addAttribute("editor", editor);
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
            EditorResponseTo updated = apiClient.updateEditor(id, editor);
            model.addAttribute("success", true);
            model.addAttribute("message", "Editor updated successfully!");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "editors/form";
        }

        return "redirect:/editors";
    }

    @PostMapping("/{id}/delete")
    public String deleteEditor(@PathVariable Long id, Model model) {
        try {
            apiClient.deleteEditor(id);
            model.addAttribute("success", true);
            model.addAttribute("message", "Editor deleted successfully!");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "redirect:/editors";
    }
}

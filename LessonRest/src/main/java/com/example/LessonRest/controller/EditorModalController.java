package com.example.LessonRest.controller;

import com.example.LessonRest.dto.EditorRequestTo;
import com.example.LessonRest.dto.EditorResponseTo;
import com.example.LessonRest.repository.EditorRepository;
import com.example.LessonRest.mapper.EditorMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/modal/editors")
public class EditorModalController {

    @Autowired
    private EditorRepository editorRepository;

    @Autowired
    private EditorMapper editorMapper;

    @GetMapping
    public String listEditorsForModal(Model model) {
        model.addAttribute("editor", new EditorRequestTo());
        return "editors/modal";
    }

    @GetMapping("/{id}")
    public ResponseEntity<EditorResponseTo> getEditorByIdFromModal(@PathVariable Long id) {
        Optional<com.example.LessonRest.entity.Editor> optionalEditor = editorRepository.findById(id);
        if (optionalEditor.isPresent()) {
            com.example.LessonRest.entity.Editor editor = optionalEditor.get();
            EditorResponseTo response = editorMapper.toResponseTo(editor);
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public String createEditorInModal(@Valid @ModelAttribute("editor") EditorRequestTo editor,
                                        BindingResult result,
                                        Model model) {
        if (result.hasErrors()) {
            List<EditorResponseTo> editors = editorRepository.findAll().stream()
                    .map(editorMapper::toResponseTo)
                    .collect(ArrayList::new, (list, item) -> list.add(item), List::addAll);
            model.addAttribute("editors", editors);
            model.addAttribute("error", "Validation failed: " + result.getAllErrors().get(0).getDefaultMessage());
            return "editors/list";
        }

        try {
            var entity = editorMapper.toEntity(editor);
            var saved = editorRepository.save(entity);
            var response = editorMapper.toResponseTo(saved);
            model.addAttribute("success", true);
            model.addAttribute("message", "Editor created successfully! ID: " + response.getId());
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            List<EditorResponseTo> editors = editorRepository.findAll().stream()
                    .map(editorMapper::toResponseTo)
                    .collect(ArrayList::new, (list, item) -> list.add(item), List::addAll);
            model.addAttribute("editors", editors);
            return "editors/list";
        }

        return "redirect:/editors";
    }

    @PutMapping("/{id}")
    public String updateEditorInModal(@PathVariable Long id, @Valid @ModelAttribute("editor") EditorRequestTo editor,
                                        BindingResult result,
                                        Model model) {
        if (result.hasErrors()) {
            List<EditorResponseTo> editors = editorRepository.findAll().stream()
                    .map(editorMapper::toResponseTo)
                    .collect(ArrayList::new, (list, item) -> list.add(item), List::addAll);
            model.addAttribute("editors", editors);
            model.addAttribute("error", "Validation failed: " + result.getAllErrors().get(0).getDefaultMessage());
            return "editors/list";
        }

        try {
            var entity = editorRepository.findById(id).orElse(null);
            if (entity != null) {
                entity.setLogin(editor.getLogin());
                entity.setFirstname(editor.getFirstname());
                entity.setLastname(editor.getLastname());
                // Обновляем пароль только если он не пустой
                if (editor.getPassword() != null && !editor.getPassword().trim().isEmpty()) {
                    entity.setPassword(editor.getPassword());
                }
                editorRepository.save(entity);
                model.addAttribute("success", true);
                model.addAttribute("message", "Editor updated successfully!");
            } else {
                model.addAttribute("error", "Editor not found");
                List<EditorResponseTo> editors = editorRepository.findAll().stream()
                        .map(editorMapper::toResponseTo)
                        .collect(ArrayList::new, (list, item) -> list.add(item), List::addAll);
                model.addAttribute("editors", editors);
                return "editors/list";
            }
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            List<EditorResponseTo> editors = editorRepository.findAll().stream()
                    .map(editorMapper::toResponseTo)
                    .collect(ArrayList::new, (list, item) -> list.add(item), List::addAll);
            model.addAttribute("editors", editors);
            return "editors/list";
        }

        return "redirect:/editors";
    }
}

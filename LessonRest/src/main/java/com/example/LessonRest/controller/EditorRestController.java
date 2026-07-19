package com.example.LessonRest.controller;

import com.example.LessonRest.dto.EditorRequestTo;
import com.example.LessonRest.dto.EditorResponseTo;
import com.example.LessonRest.repository.EditorRepository;
import com.example.LessonRest.mapper.EditorMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1.0/editors")
public class EditorRestController {

    @Autowired
    private EditorRepository editorRepository;

    @Autowired
    private EditorMapper editorMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public List<EditorResponseTo> getAllEditors() {
        return editorRepository.findAll().stream()
                .map(editorMapper::toResponseTo)
                .toList();
    }

    @GetMapping("/{id}")
    public EditorResponseTo getEditorById(@PathVariable Long id) {
        Optional<com.example.LessonRest.entity.Editor> optionalEditor = editorRepository.findById(id);
        return optionalEditor.map(editorMapper::toResponseTo).orElse(null);
    }

    @PostMapping
    public ResponseEntity<EditorResponseTo> createEditor(@Valid @RequestBody EditorRequestTo request) {
        com.example.LessonRest.entity.Editor entity = editorMapper.toEntity(request);
        // Хешируем пароль перед сохранением
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        entity.setPassword(encodedPassword);
        com.example.LessonRest.entity.Editor saved = editorRepository.save(entity);
        EditorResponseTo response = editorMapper.toResponseTo(saved);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EditorResponseTo> updateEditor(@PathVariable Long id, @Valid @RequestBody EditorRequestTo request) {
        Optional<com.example.LessonRest.entity.Editor> optionalEditor = editorRepository.findById(id);
        if (optionalEditor.isPresent()) {
            com.example.LessonRest.entity.Editor entity = optionalEditor.get();
            entity.setLogin(request.getLogin());
            entity.setFirstname(request.getFirstname());
            entity.setLastname(request.getLastname());
            // Обновляем пароль только если он не пустой
            if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
                // Хешируем пароль перед сохранением
                String encodedPassword = passwordEncoder.encode(request.getPassword());
                entity.setPassword(encodedPassword);
            }
            com.example.LessonRest.entity.Editor saved = editorRepository.save(entity);
            EditorResponseTo response = editorMapper.toResponseTo(saved);
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEditor(@PathVariable Long id) {
        Optional<com.example.LessonRest.entity.Editor> optionalEditor = editorRepository.findById(id);
        if (optionalEditor.isPresent()) {
            editorRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

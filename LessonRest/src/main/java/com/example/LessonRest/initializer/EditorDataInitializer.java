package com.example.LessonRest.initializer;

import com.example.LessonRest.entity.Editor;
import com.example.LessonRest.repository.EditorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Order(1)
@Component
public class EditorDataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(EditorDataInitializer.class);

    private final EditorRepository editorRepository;
    private final PasswordEncoder passwordEncoder;

    public EditorDataInitializer(EditorRepository editorRepository, PasswordEncoder passwordEncoder) {
        this.editorRepository = editorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Создаем редактора 'editor' если он не существует
        if (editorRepository.findByLogin("editor").isEmpty()) {
            Editor editor = new Editor();
            editor.setLogin("editor");
            editor.setPassword(passwordEncoder.encode("editor"));
            editor.setFirstname("Editor");
            editor.setLastname("User");
            
            editorRepository.save(editor);
            logger.info("Editor 'editor' created successfully!");
        } else {
            logger.info("Editor 'editor' already exists.");
        }
        
        // Создаем редактора Дмитрия Попкова если он не существует
        if (editorRepository.findByLogin("rutcha@rambler.ru").isEmpty()) {
            Editor editor = new Editor();
            editor.setLogin("rutcha@rambler.ru");
            editor.setPassword(passwordEncoder.encode("password123"));
            editor.setFirstname("Дмитрий");
            editor.setLastname("Попков");
            
            editorRepository.save(editor);
            logger.info("Editor 'rutcha@rambler.ru' (Дмитрий Попков) created successfully!");
        } else {
            logger.info("Editor 'rutcha@rambler.ru' (Дмитрий Попков) already exists.");
        }
    }
}

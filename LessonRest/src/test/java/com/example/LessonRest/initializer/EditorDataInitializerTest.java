package com.example.LessonRest.initializer;

import com.example.LessonRest.entity.Editor;
import com.example.LessonRest.repository.EditorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.*;

class EditorDataInitializerTest {

    @Mock
    private EditorRepository editorRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private EditorDataInitializer initializer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        initializer = new EditorDataInitializer(editorRepository, passwordEncoder);
    }

    @Test
    void testRun_NoExistingEditors() throws Exception {
        // Given
        when(editorRepository.findByLogin("editor")).thenReturn(Optional.empty());
        when(editorRepository.findByLogin("rutcha@rambler.ru")).thenReturn(Optional.empty());

        // When
        initializer.run();

        // Then - два редактора создаются, поэтому save вызывается 2 раза
        verify(editorRepository, times(2)).findByLogin(any(String.class));
        verify(editorRepository, times(2)).save(any(Editor.class));
    }

    @Test
    void testRun_EditorAlreadyExists() throws Exception {
        // Given
        Editor existingEditor = new Editor();
        existingEditor.setLogin("editor");
        when(editorRepository.findByLogin("editor")).thenReturn(Optional.of(existingEditor));
        when(editorRepository.findByLogin("rutcha@rambler.ru")).thenReturn(Optional.empty());

        // When
        initializer.run();

        // Then
        verify(editorRepository, times(1)).findByLogin("editor");
        verify(editorRepository, times(1)).findByLogin("rutcha@rambler.ru");
        verify(editorRepository, times(1)).save(any(Editor.class)); // Only rutcha@rambler.ru should be saved
    }

    @Test
    void testRun_BothEditorsAlreadyExists() throws Exception {
        // Given
        Editor existingEditor1 = new Editor();
        existingEditor1.setLogin("editor");
        Editor existingEditor2 = new Editor();
        existingEditor2.setLogin("rutcha@rambler.ru");
        
        when(editorRepository.findByLogin("editor")).thenReturn(Optional.of(existingEditor1));
        when(editorRepository.findByLogin("rutcha@rambler.ru")).thenReturn(Optional.of(existingEditor2));

        // When
        initializer.run();

        // Then
        verify(editorRepository, times(1)).findByLogin("editor");
        verify(editorRepository, times(1)).findByLogin("rutcha@rambler.ru");
        verify(editorRepository, times(0)).save(any(Editor.class));
    }
}

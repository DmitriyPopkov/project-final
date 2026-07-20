package com.example.LessonRest.service;

import com.example.LessonRest.dto.LoginRequestTo;

import com.example.LessonRest.entity.Editor;
import com.example.LessonRest.repository.EditorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class EditorAuthServiceTest {

    @Mock
    private EditorRepository editorRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private EditorAuthService editorAuthService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ✅ Тест: успешная аутентификация с правильным паролем
    @Test
    void testAuthenticate_SuccessWithEncodedPassword() {
        // Given
        LoginRequestTo request = new LoginRequestTo();
        request.setUsername("rutcha@rambler.ru");
        request.setPassword("password123");

        Editor editor = new Editor();
        editor.setLogin("rutcha@rambler.ru");
        editor.setPassword("encoded_password");

        when(editorRepository.findByLogin("rutcha@rambler.ru")).thenReturn(Optional.of(editor));
        when(passwordEncoder.matches("password123", "encoded_password")).thenReturn(true);

        // When
        var result = editorAuthService.authenticate(request);

        // Then
        assertNotNull(result);
        assertEquals("rutcha@rambler.ru", result.getUsername());
        verify(passwordEncoder, times(1)).matches("password123", "encoded_password");
    }

    // ✅ Тест: аутентификация завершается неудачей, если пароль не подходит
    @Test
    void testAuthenticate_InvalidPassword() {
        // Given
        LoginRequestTo request = new LoginRequestTo();
        request.setUsername("rutcha@rambler.ru");
        request.setPassword("wrong_password");

        Editor editor = new Editor();
        editor.setLogin("rutcha@rambler.ru");
        editor.setPassword("encoded_password");

        when(editorRepository.findByLogin("rutcha@rambler.ru")).thenReturn(Optional.of(editor));
        when(passwordEncoder.matches("wrong_password", "encoded_password")).thenReturn(false);

        // When
        var result = editorAuthService.authenticate(request);

        // Then
        assertNull(result);
        verify(passwordEncoder, times(1)).matches("wrong_password", "encoded_password");
    }

    // ✅ Тест: аутентификация завершается неудачей, если редактор не найден
    @Test
    void testAuthenticate_EditorNotFound() {
        // Given
        LoginRequestTo request = new LoginRequestTo();
        request.setUsername("nonexistent");
        request.setPassword("password123");

        when(editorRepository.findByLogin("nonexistent")).thenReturn(Optional.empty());

        // When
        var result = editorAuthService.authenticate(request);

        // Then
        assertNull(result);
        verify(passwordEncoder, never()).matches(any(), any());
    }

    // ✅ Тест: получение профиля успешно
    @Test
    void testGetProfile_Success() {
        // Given
        String login = "rutcha@rambler.ru";
        Editor editor = new Editor();
        editor.setId(1L);
        editor.setLogin(login);
        editor.setFirstname("Дмитрий");
        editor.setLastname("Попков");

        when(editorRepository.findByLogin(login)).thenReturn(Optional.of(editor));

        // When
        var result = editorAuthService.getProfile(login);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(login, result.getLogin());
        assertEquals("Дмитрий", result.getFirstname());
        assertEquals("Попков", result.getLastname());
    }

    // ✅ Тест: получение профиля — редактор не найден
    @Test
    void testGetProfile_EditorNotFound() {
        // Given
        String login = "nonexistent";

        when(editorRepository.findByLogin(login)).thenReturn(Optional.empty());

        // When
        var result = editorAuthService.getProfile(login);

        // Then
        assertNull(result);
    }






}
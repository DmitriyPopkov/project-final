package com.example.LessonRest.service;

import com.example.LessonRest.dto.LoginRequestTo;
import com.example.LessonRest.dto.RegisterRequestTo;
import com.example.LessonRest.entity.Role;
import com.example.LessonRest.entity.User;
import com.example.LessonRest.repository.RoleRepository;
import com.example.LessonRest.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private org.springframework.security.authentication.AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser_Success() {
        // Given
        RegisterRequestTo request = new RegisterRequestTo();
        request.setUsername("testuser");
        request.setPassword("password123");
        request.setConfirmPassword("password123");
        request.setRole("USER");

        Role userRole = new Role();
        userRole.setName("USER");

        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded_password");
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        User result = authService.registerUser(request);

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterUser_PasswordsDoNotMatch() {
        // Given
        RegisterRequestTo request = new RegisterRequestTo();
        request.setUsername("testuser");
        request.setPassword("password123");
        request.setConfirmPassword("password456");
        request.setRole("USER");

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            authService.registerUser(request);
        });
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegisterUser_UsernameAlreadyExists() {
        // Given
        RegisterRequestTo request = new RegisterRequestTo();
        request.setUsername("testuser");
        request.setPassword("password123");
        request.setConfirmPassword("password123");
        request.setRole("USER");

        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            authService.registerUser(request);
        });
        verify(userRepository, never()).save(any(User.class));
    }
}

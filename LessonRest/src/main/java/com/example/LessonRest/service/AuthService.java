package com.example.LessonRest.service;

import com.example.LessonRest.dto.AuthResponseTo;
import com.example.LessonRest.dto.LoginRequestTo;
import com.example.LessonRest.dto.RegisterRequestTo;
import com.example.LessonRest.entity.Role;
import com.example.LessonRest.entity.User;
import com.example.LessonRest.repository.RoleRepository;
import com.example.LessonRest.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthResponseTo authenticate(LoginRequestTo request) {
        // Аутентификация пользователя
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // Устанавливаем аутентификацию в контексте безопасности
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Генерируем токен (в простом варианте используем username как токен)
        // В реальном приложении здесь должна быть генерация JWT токена
        String accessToken = generateToken(request.getUsername());

        return new AuthResponseTo(accessToken);
    }

    public User registerUser(RegisterRequestTo request) {
        // Проверка паролей
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }
        
        String role = request.getRole();
        
        // Для роли USER - создаем нового пользователя
        if ("USER".equalsIgnoreCase(role)) {
            // Проверка существования пользователя
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new RuntimeException("Username already exists");
            }

            User user = new User();
            user.setUsername(request.getUsername());
            
            // Хеширование пароля
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setEnabled(true);

            Role userRole = roleRepository.findByName("USER")
                    .orElseThrow(() -> new RuntimeException("User role not found"));
            user.setRole(userRole);

            User savedUser = userRepository.save(user);
            
            // Автоматическая аутентификация после регистрации
            authenticate(new LoginRequestTo(request.getUsername(), request.getPassword()));
            
            return savedUser;
        }
        // Для роли ADMIN - ищем существующего пользователя "admin" в БД
        else if ("ADMIN".equalsIgnoreCase(role)) {
            User user = userRepository.findByUsername("admin")
                    .orElseThrow(() -> new RuntimeException("User 'admin' not found in database. Please contact administrator."));
            
            // Проверяем, совпадает ли пароль
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new RuntimeException("Invalid password for user 'admin'");
            }
            
            return user;
        }
        // Для роли EDITOR - создаем нового пользователя
        else if ("EDITOR".equalsIgnoreCase(role)) {
            // Проверка существования пользователя
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new RuntimeException("Username already exists");
            }

            User user = new User();
            user.setUsername(request.getUsername());
            
            // Хеширование пароля
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setEnabled(true);

            Role editorRole = roleRepository.findByName("EDITOR")
                    .orElseThrow(() -> new RuntimeException("Editor role not found"));
            user.setRole(editorRole);

            return userRepository.save(user);
        }
        
        throw new RuntimeException("Invalid role: " + role);
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser")) {
            String username = authentication.getName();
            return userRepository.findByUsernameWithRole(username).orElse(null);
        }
        
        return null;
    }

    private String generateToken(String username) {
        // В простом варианте используем base64-encoded token
        // В реальном приложении здесь должна быть генерация JWT токена
        return "Bearer " + java.util.Base64.getEncoder().encodeToString(username.getBytes());
    }
}

package com.example.LessonRest.service;

import com.example.LessonRest.dto.RegisterRequestTo;
import com.example.LessonRest.entity.Role;
import com.example.LessonRest.entity.User;
import com.example.LessonRest.repository.RoleRepository;
import com.example.LessonRest.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(RegisterRequestTo registerRequestTo) {
        String role = registerRequestTo.getRole();
        
        // Для ролей USER и ADMIN - проверяем существование в БД
        if ("USER".equalsIgnoreCase(role)) {
            User user = userRepository.findByUsername("user")
                    .orElseThrow(() -> new RuntimeException("User 'user' not found in database. Please contact administrator."));
            
            // Проверяем, совпадает ли пароль
            if (!passwordEncoder.matches(registerRequestTo.getPassword(), user.getPassword())) {
                throw new RuntimeException("Invalid password for user 'user'");
            }
            
            return user;
        } else if ("ADMIN".equalsIgnoreCase(role)) {
            User user = userRepository.findByUsername("admin")
                    .orElseThrow(() -> new RuntimeException("User 'admin' not found in database. Please contact administrator."));
            
            // Проверяем, совпадает ли пароль
            if (!passwordEncoder.matches(registerRequestTo.getPassword(), user.getPassword())) {
                throw new RuntimeException("Invalid password for user 'admin'");
            }
            
            return user;
        } else if ("EDITOR".equalsIgnoreCase(role)) {
            // Проверка существования пользователя
            if (userRepository.existsByUsername(registerRequestTo.getUsername())) {
                throw new RuntimeException("Username already exists");
            }

            User user = new User();
            user.setUsername(registerRequestTo.getUsername());
            
            // Хеширование пароля
            user.setPassword(passwordEncoder.encode(registerRequestTo.getPassword()));
            user.setEnabled(true);

            Role editorRole = roleRepository.findByName("EDITOR")
                    .orElseThrow(() -> new RuntimeException("Editor role not found"));
            user.setRole(editorRole);

            return userRepository.save(user);
        }
        
        throw new RuntimeException("Invalid role: " + role);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public boolean hasRole(User user, String roleName) {
        return user.getRole() != null && user.getRole().getName().equals(roleName);
    }
}

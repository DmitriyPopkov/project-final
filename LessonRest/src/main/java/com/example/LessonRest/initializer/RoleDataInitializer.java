package com.example.LessonRest.initializer;

import com.example.LessonRest.entity.Role;
import com.example.LessonRest.entity.User;
import com.example.LessonRest.repository.RoleRepository;
import com.example.LessonRest.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Order(5)
@Component
public class RoleDataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(RoleDataInitializer.class);

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RoleDataInitializer(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Создаем роли если они не существуют
        List<String> roleNames = Arrays.asList("ADMIN", "USER", "EDITOR");
        
        for (String roleName : roleNames) {
            if (roleRepository.findByName(roleName).isEmpty()) {
                Role role = new Role(roleName);
                roleRepository.save(role);
                logger.info("Role '{}' created successfully!", roleName);
            } else {
                logger.info("Role '{}' already exists.", roleName);
            }
        }
        
        // Создаем пользователей admin и user в базе данных
        createUserIfNotExists("admin", "admin", "ADMIN");
        createUserIfNotExists("user", "user", "USER");
        createUserIfNotExists("editor", "editor", "EDITOR");
    }

    private void createUserIfNotExists(String username, String password, String roleName) {
        if (userRepository.findByUsername(username).isEmpty()) {
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
            
            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setEnabled(true);
            user.setRole(role);
            
            userRepository.save(user);
            logger.info("User '{}' created successfully with role '{}'!", username, roleName);
        } else {
            logger.info("User '{}' already exists.", username);
        }
    }
}

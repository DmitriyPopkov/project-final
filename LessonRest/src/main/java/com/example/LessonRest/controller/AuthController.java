package com.example.LessonRest.controller;

import com.example.LessonRest.dto.AuthResponseTo;
import com.example.LessonRest.dto.EditorProfileResponseTo;
import com.example.LessonRest.dto.LoginRequestTo;
import com.example.LessonRest.dto.ProfileResponseTo;
import com.example.LessonRest.dto.RegisterRequestTo;
import com.example.LessonRest.entity.User;
import com.example.LessonRest.service.AuthService;
import com.example.LessonRest.service.EditorAuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1.0/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private EditorAuthService editorAuthService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseTo> register(@Valid @RequestBody RegisterRequestTo request) {
        User user = authService.registerUser(request);
        
        // Если пользователь был создан или найден в БД (USER или ADMIN)
        if (user != null && (user.getId() != null || user.getUsername().equals("user") || user.getUsername().equals("admin"))) {
            // Логиним пользователя автоматически
            LoginRequestTo loginRequest = new LoginRequestTo();
            loginRequest.setUsername(request.getUsername());
            loginRequest.setPassword(request.getPassword());
            
            AuthResponseTo response = authService.authenticate(loginRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        
        // Для USER и ADMIN (предустановленные) возвращаем ошибку - они не регистрируются
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseTo> login(@Valid @RequestBody LoginRequestTo request) {
        AuthResponseTo response = authService.authenticate(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login-editor")
    public ResponseEntity<AuthResponseTo> loginEditor(@Valid @RequestBody LoginRequestTo request, HttpServletResponse response) {
        AuthResponseTo responseDto = editorAuthService.authenticate(request);
        if (responseDto != null) {
            // Сохраняем editorId в cookie для использования в контроллерах
            // Срок действия cookie - 1 час
            Cookie editorIdCookie = new Cookie("editorId", String.valueOf(responseDto.getEditorId()));
            editorIdCookie.setPath("/");
            editorIdCookie.setMaxAge(3600); // 1 час
            response.addCookie(editorIdCookie);
            
            return ResponseEntity.ok(responseDto);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/editor-profile")
    public ResponseEntity<EditorProfileResponseTo> getEditorProfile(@RequestParam String login) {
        EditorProfileResponseTo profile = editorAuthService.getProfile(login);
        if (profile != null) {
            return ResponseEntity.ok(profile);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/profile")
    public ResponseEntity<ProfileResponseTo> profile() {
        User user = authService.getCurrentUser();
        if (user != null) {
            ProfileResponseTo profile = new ProfileResponseTo();
            profile.setId(user.getId());
            profile.setUsername(user.getUsername());
            profile.setRole(user.getRoleName());
            return ResponseEntity.ok(profile);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}

package com.example.LessonRest.service;

import com.example.LessonRest.dto.AuthResponseTo;
import com.example.LessonRest.dto.EditorProfileResponseTo;
import com.example.LessonRest.dto.LoginRequestTo;
import com.example.LessonRest.entity.Editor;
import com.example.LessonRest.repository.EditorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class EditorAuthService {

    @Autowired
    private EditorRepository editorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Аутентификация редактора по логину и паролю
     * @param request - данные для входа
     * @return AuthResponseTo с токеном и профилем редактора или null если аутентификация не удалась
     */
    public AuthResponseTo authenticate(LoginRequestTo request) {
        Editor editor = editorRepository.findByLogin(request.getUsername())
                .orElse(null);

        if (editor != null) {
            String storedPassword = editor.getPassword();
            
            // Сначала проверяем как plain text (для совместимости)
            if (storedPassword.equals(request.getPassword())) {
                // Аутентификация успешна
                String accessToken = generateToken(editor.getLogin());
                // Возвращаем AuthResponseTo с профилем редактора и editorId
                AuthResponseTo response = new AuthResponseTo(
                    editor.getLogin(),
                    "EDITOR",
                    editor.getFirstname(),
                    editor.getLastname()
                );
                response.setEditorId(editor.getId());
                return response;
            }
            
            // Затем проверяем как BCrypt хеш
            if (passwordEncoder.matches(request.getPassword(), storedPassword)) {
                // Аутентификация успешна
                String accessToken = generateToken(editor.getLogin());
                // Возвращаем AuthResponseTo с профилем редактора и editorId
                AuthResponseTo response = new AuthResponseTo(
                    editor.getLogin(),
                    "EDITOR",
                    editor.getFirstname(),
                    editor.getLastname()
                );
                response.setEditorId(editor.getId());
                return response;
            }
        }

        return null;
    }

    /**
     * Получение профиля редактора по логину
     * @param login - логин редактора
     * @return EditorProfileResponseTo или null если не найден
     */
    public EditorProfileResponseTo getProfile(String login) {
        return editorRepository.findByLogin(login)
                .map(editor -> new EditorProfileResponseTo(
                        editor.getId(),
                        editor.getLogin(),
                        editor.getFirstname(),
                        editor.getLastname()
                ))
                .orElse(null);
    }

    private String generateToken(String login) {
        // В простом варианте используем base64-encoded token
        // В реальном приложении здесь должна быть генерация JWT токена
        return "Bearer " + java.util.Base64.getEncoder().encodeToString(login.getBytes());
    }
}

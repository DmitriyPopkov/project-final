package com.example.LessonRest.service;

import com.example.LessonRest.dto.AuthenticationDetails;
import com.example.LessonRest.entity.Editor;
import com.example.LessonRest.entity.User;
import com.example.LessonRest.repository.EditorRepository;
import com.example.LessonRest.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EditorRepository editorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();
        
        // Get the AuthenticationDetails if available
        boolean isEditorLogin = false;
        if (authentication.getDetails() instanceof AuthenticationDetails details) {
            isEditorLogin = details.isEditorLogin();
        }

        // Если isEditorLogin=true или логин содержит @, ищем в tbl_editors
        boolean isEmail = username.contains("@") || isEditorLogin;

        if (isEmail) {
            // Ищем в tbl_editors
            Optional<Editor> editorOpt = editorRepository.findByLogin(username);
            if (editorOpt.isPresent()) {
                Editor editor = editorOpt.get();
                if (passwordEncoder.matches(password, editor.getPassword())) {
                    return new UsernamePasswordAuthenticationToken(
                            editor.getLogin(),
                            editor.getPassword(),
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_EDITOR"))
                    );
                }
                throw new BadCredentialsException("Invalid credentials");
            }

            // Если не найден в tbl_editors и это user/user, ищем в tbl_users
            if ("user".equals(username)) {
                Optional<User> userOpt = userRepository.findByUsernameWithRole(username);
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    if (passwordEncoder.matches(password, user.getPassword())) {
                        return new UsernamePasswordAuthenticationToken(
                                user.getUsername(),
                                user.getPassword(),
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRoleName()))
                        );
                    }
                    throw new BadCredentialsException("Invalid credentials");
                }
            }

            throw new BadCredentialsException("Editor not found with login: " + username);
        } else {
            // Ищем в tbl_users
            Optional<User> userOpt = userRepository.findByUsernameWithRole(username);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                if (passwordEncoder.matches(password, user.getPassword())) {
                    return new UsernamePasswordAuthenticationToken(
                            user.getUsername(),
                            user.getPassword(),
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRoleName()))
                    );
                }
                throw new BadCredentialsException("Invalid credentials");
            }
            throw new BadCredentialsException("User not found with username: " + username);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}

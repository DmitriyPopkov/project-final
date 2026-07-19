package com.example.LessonRest.service;

import com.example.LessonRest.entity.Editor;
import com.example.LessonRest.entity.User;
import com.example.LessonRest.repository.EditorRepository;
import com.example.LessonRest.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EditorRepository editorRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return loadUserByUsername(username, false);
    }

    public UserDetails loadUserByUsernameFromEditorTable(String username) throws UsernameNotFoundException {
        return loadUserByUsername(username, true);
    }

    private UserDetails loadUserByUsername(String username, boolean forceEditor) throws UsernameNotFoundException {
        // Если forceEditor=true или логин содержит @, ищем в tbl_editors
        boolean isEmail = username.contains("@") || forceEditor;
        
        if (isEmail) {
            // Ищем в tbl_editors
            Optional<Editor> editorOpt = editorRepository.findByLogin(username);
            if (editorOpt.isPresent()) {
                Editor editor = editorOpt.get();
                // Editor получает права EDITOR (при галочке вход для редактора)
                return new org.springframework.security.core.userdetails.User(
                        editor.getLogin(),
                        editor.getPassword(),
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_EDITOR"))
                );
            }
            
            // Если не найден в tbl_editors и это user/user, ищем в tbl_users
            if ("user".equals(username)) {
                Optional<User> userOpt = userRepository.findByUsernameWithRole(username);
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    return new org.springframework.security.core.userdetails.User(
                            user.getUsername(),
                            user.getPassword(),
                            mapRolesToAuthorities(user.getRoleName())
                    );
                }
            }
            
            throw new UsernameNotFoundException("Editor not found with login: " + username);
        } else {
            // Ищем в tbl_users
            Optional<User> userOpt = userRepository.findByUsernameWithRole(username);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                return new org.springframework.security.core.userdetails.User(
                        user.getUsername(),
                        user.getPassword(),
                        mapRolesToAuthorities(user.getRoleName())
                );
            }
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(String roleName) {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + roleName));
    }
}

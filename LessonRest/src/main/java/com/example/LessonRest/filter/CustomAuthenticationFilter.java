package com.example.LessonRest.filter;

import com.example.LessonRest.dto.AuthenticationDetails;
import com.example.LessonRest.service.CustomUserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final CustomUserDetailsService userDetailsService;

    public CustomAuthenticationFilter(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
        setFilterProcessesUrl("/login");
        
        // Настройка обработчика успешной аутентификации
        SimpleUrlAuthenticationSuccessHandler successHandler = new SimpleUrlAuthenticationSuccessHandler();
        successHandler.setDefaultTargetUrl("/index");
        setAuthenticationSuccessHandler(successHandler);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        // Extract editorLogin parameter to determine authentication type
        boolean isEditorLogin = "true".equals(request.getParameter("editorLogin"));
        
        // Create authentication token with the user details and AuthenticationDetails
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authenticationToken = 
                new UsernamePasswordAuthenticationToken(userDetails, password);
            // Set AuthenticationDetails on the token
            authenticationToken.setDetails(new AuthenticationDetails(isEditorLogin));
            return authenticationToken;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

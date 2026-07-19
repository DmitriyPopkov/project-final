package com.example.LessonRest.controller;

import com.example.LessonRest.entity.User;
import com.example.LessonRest.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Главный контроллер для отображения стартовой страницы
 */
@Controller
public class MainController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String index(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = null;
        if (authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser")) {
            currentUser = userRepository.findByUsernameWithRole(authentication.getName()).orElse(null);
        }
        
        if (currentUser != null) {
            model.addAttribute("currentUserRole", currentUser.getUsername());
            model.addAttribute("currentUserRoleName", currentUser.getRoleName());
        } else {
            model.addAttribute("currentUserRole", null);
            model.addAttribute("currentUserRoleName", null);
        }
        
        return "index";
    }

    @GetMapping("/index")
    public String indexPage(Model model) {
        return index(model);
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid username or password.");
        }
        return "login";
    }
}

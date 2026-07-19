package com.example.LessonRest.controller;

import com.example.LessonRest.dto.StickerRequestTo;
import com.example.LessonRest.dto.StickerResponseTo;
import com.example.LessonRest.entity.User;
import com.example.LessonRest.repository.StickerRepository;
import com.example.LessonRest.repository.UserRepository;
import com.example.LessonRest.mapper.StickerMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/stickers")
public class StickerController {

    @Autowired
    private StickerRepository stickerRepository;

    @Autowired
    private StickerMapper stickerMapper;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String listStickers(Model model) {
        List<StickerResponseTo> stickers = stickerRepository.findAll().stream()
                .map(stickerMapper::toResponseTo)
                .collect(ArrayList::new, (list, item) -> list.add(item), List::addAll);
        model.addAttribute("stickers", stickers);
        
        // Получаем текущего пользователя и его роль
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = null;
        if (authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser")) {
            currentUser = userRepository.findByUsernameWithRole(authentication.getName()).orElse(null);
        }
        if (currentUser != null) {
            model.addAttribute("currentUserRole", currentUser.getRoleName());
        }
        
        return "stickers/list";
    }

    @GetMapping("/{id}")
    public String getStickerById(@PathVariable Long id, Model model) {
        StickerResponseTo sticker = stickerRepository.findById(id).map(stickerMapper::toResponseTo).orElse(null);
        if (sticker != null) {
            model.addAttribute("sticker", sticker);
            
            // Получаем текущего пользователя и его роль
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = null;
            if (authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser")) {
                currentUser = userRepository.findByUsernameWithRole(authentication.getName()).orElse(null);
            }
            if (currentUser != null) {
                model.addAttribute("currentUserRole", currentUser.getRoleName());
            }
            
            return "stickers/detail";
        } else {
            model.addAttribute("error", "Sticker not found");
            return "error";
        }
    }

    @GetMapping("/new")
    public String showNewForm(Model model) {
        model.addAttribute("sticker", new StickerRequestTo());
        return "stickers/form";
    }

    @PostMapping
    public String createSticker(@ModelAttribute("sticker") StickerRequestTo sticker) {
        var entity = stickerMapper.toEntity(sticker);
        stickerRepository.save(entity);
        return "redirect:/stickers";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        StickerResponseTo sticker = stickerRepository.findById(id).map(stickerMapper::toResponseTo).orElse(null);
        if (sticker != null) {
            StickerRequestTo request = stickerMapper.toRequestTo(sticker);
            model.addAttribute("sticker", request);
            return "stickers/form";
        } else {
            model.addAttribute("error", "Sticker not found");
            return "error";
        }
    }

    @PostMapping("/{id}")
    public String updateSticker(@PathVariable Long id, @RequestParam String name, @RequestParam String color) {
        var entity = stickerRepository.findById(id).orElse(null);
        if (entity != null) {
            entity.setName(name);
            entity.setColor(color);
            stickerRepository.save(entity);
        }
        return "redirect:/stickers";
    }

    @PostMapping("/{id}/delete")
    public String deleteSticker(@PathVariable Long id) {
        stickerRepository.deleteById(id);
        return "redirect:/stickers";
    }
}

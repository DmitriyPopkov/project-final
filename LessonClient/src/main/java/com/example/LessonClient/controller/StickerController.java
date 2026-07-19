package com.example.LessonClient.controller;

import com.example.LessonClient.dto.StickerRequestTo;
import com.example.LessonClient.dto.StickerResponseTo;
import com.example.LessonClient.service.ApiClient;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/stickers")
public class StickerController {

    @Autowired
    private ApiClient apiClient;

    @GetMapping
    public String listStickers(@RequestParam(required = false) String name, Model model) {
        List<StickerResponseTo> stickers = new ArrayList<>();
        
        if (name != null && !name.isEmpty()) {
            List<StickerResponseTo> allStickers = apiClient.getAllStickers().collectList().block();
            if (allStickers != null) {
                stickers = allStickers.stream()
                        .filter(s -> s.getName().toLowerCase().contains(name.toLowerCase()))
                        .collect(Collectors.toList());
            }
        } else {
            stickers = apiClient.getAllStickers().collectList().block();
            if (stickers == null) stickers = new ArrayList<>();
        }
        
        model.addAttribute("stickers", stickers);
        model.addAttribute("searchName", name);
        return "stickers/list";
    }

    @GetMapping("/{id}")
    public String getStickerById(@PathVariable Long id, Model model) {
        StickerResponseTo sticker = apiClient.getStickerById(id);
        model.addAttribute("sticker", sticker);
        return "stickers/detail";
    }

    @GetMapping("/create")
    public String createStickerForm(Model model) {
        model.addAttribute("sticker", new StickerRequestTo());
        return "stickers/form";
    }

    @PostMapping("/create")
    public String createSticker(@Valid @ModelAttribute("sticker") StickerRequestTo sticker,
                                BindingResult result,
                                Model model) {
        if (result.hasErrors()) {
            return "stickers/form";
        }

        try {
            StickerResponseTo created = apiClient.createSticker(sticker);
            model.addAttribute("success", true);
            model.addAttribute("message", "Sticker created successfully! ID: " + created.getId());
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "stickers/form";
        }

        return "redirect:/stickers";
    }

    @PostMapping("/{id}/edit")
    public String updateSticker(@PathVariable Long id, @Valid @ModelAttribute("sticker") StickerRequestTo sticker,
                                BindingResult result,
                                Model model) {
        if (result.hasErrors()) {
            return "stickers/form";
        }

        try {
            StickerResponseTo updated = apiClient.updateSticker(id, sticker);
            model.addAttribute("success", true);
            model.addAttribute("message", "Sticker updated successfully!");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "stickers/form";
        }

        return "redirect:/stickers";
    }

    @PostMapping("/{id}/delete")
    public String deleteSticker(@PathVariable Long id, Model model) {
        try {
            apiClient.deleteSticker(id);
            model.addAttribute("success", true);
            model.addAttribute("message", "Sticker deleted successfully!");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "redirect:/stickers";
    }
}

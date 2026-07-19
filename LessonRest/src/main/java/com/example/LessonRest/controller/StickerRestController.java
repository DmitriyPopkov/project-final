package com.example.LessonRest.controller;

import com.example.LessonRest.dto.StickerRequestTo;
import com.example.LessonRest.dto.StickerResponseTo;
import com.example.LessonRest.entity.Sticker;
import com.example.LessonRest.repository.StickerRepository;
import com.example.LessonRest.mapper.StickerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1.0/stickers")
public class StickerRestController {

    @Autowired
    private StickerRepository stickerRepository;

    @Autowired
    private StickerMapper stickerMapper;

    @GetMapping
    public List<StickerResponseTo> getAllStickers() {
        return stickerRepository.findAll().stream()
                .map(stickerMapper::toResponseTo)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public StickerResponseTo getStickerById(@PathVariable Long id) {
        return stickerRepository.findById(id)
                .map(stickerMapper::toResponseTo)
                .orElse(null);
    }

    @PostMapping
    public ResponseEntity<StickerResponseTo> createSticker(@RequestBody StickerRequestTo request) {
        Sticker sticker = stickerMapper.toEntity(request);
        Sticker saved = stickerRepository.save(sticker);
        StickerResponseTo response = stickerMapper.toResponseTo(saved);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StickerResponseTo> updateSticker(@PathVariable Long id, @RequestBody StickerRequestTo request) {
        Sticker sticker = stickerRepository.findById(id).orElse(null);
        if (sticker != null) {
            sticker.setName(request.getName());
            sticker.setColor(request.getColor());
            Sticker saved = stickerRepository.save(sticker);
            return ResponseEntity.ok(stickerMapper.toResponseTo(saved));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSticker(@PathVariable Long id) {
        if (stickerRepository.existsById(id)) {
            stickerRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

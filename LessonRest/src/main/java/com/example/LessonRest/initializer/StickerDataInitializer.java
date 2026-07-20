package com.example.LessonRest.initializer;

import com.example.LessonRest.entity.Sticker;
import com.example.LessonRest.repository.StickerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StickerDataInitializer implements CommandLineRunner {

    private final StickerRepository stickerRepository;

    @Override
    public void run(String... args) {
        log.info("Starting StickerDataInitializer...");

        // Проверяем, есть ли уже стикер "Природа"
        if (stickerRepository.findByName("Природа").isEmpty()) {
            log.info("Sticker 'Природа' not found. Creating new one...");

            Sticker sticker = new Sticker();
            sticker.setName("Природа");
            sticker.setColor("#4CAF50"); // зелёный цвет (hex)
            stickerRepository.save(sticker);

            log.info("✅ Sticker 'Природа' created successfully with color #4CAF50");
        } else {
            log.info("Sticker 'Природа' already exists in the database.");
        }
    }
}

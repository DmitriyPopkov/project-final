package com.example.LessonRest.repository;

import com.example.LessonRest.entity.Sticker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StickerRepository extends JpaRepository<Sticker, Long> {
    Optional<Sticker> findByName(String name);

}
package com.example.LessonRest.repository;

import com.example.LessonRest.entity.Editor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EditorRepository extends JpaRepository<Editor, Long> {
    Optional<Editor> findByLogin(String login);
}

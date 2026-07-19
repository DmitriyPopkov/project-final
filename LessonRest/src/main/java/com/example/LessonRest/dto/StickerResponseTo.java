package com.example.LessonRest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StickerResponseTo {

    private Long id;
    private String name;
    private String description;
    private Long newsId;
    private String color;
}

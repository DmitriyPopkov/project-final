package com.example.LessonClient.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StickerResponseTo {

    private Long id;
    private String name;
    private String description;
    private Long newsId;
}

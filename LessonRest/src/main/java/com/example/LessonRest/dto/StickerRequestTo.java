package com.example.LessonRest.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StickerRequestTo {

    @NotBlank(message = "Name cannot be blank")
    private String name;

    private String description;
    
    private String color;
}

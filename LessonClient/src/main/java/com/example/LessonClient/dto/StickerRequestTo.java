package com.example.LessonClient.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StickerRequestTo {

    @NotBlank(message = "Name cannot be blank")
    private String name;

    private String description;
}

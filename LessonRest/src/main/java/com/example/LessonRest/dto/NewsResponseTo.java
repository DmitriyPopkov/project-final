package com.example.LessonRest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsResponseTo {

    private Long id;
    private String title;
    private String content;
    private Long editorId;
    private List<Long> stickerIds;
    private OffsetDateTime created;
    private OffsetDateTime modified;
}

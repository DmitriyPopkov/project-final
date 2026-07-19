package com.example.LessonRest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoticeResponseTo {

    private Long id;
    private String content;
    private Long newsId;
    private String newsTitle;
}

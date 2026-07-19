package com.example.LessonClient.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeResponseTo {

    private Long id;
    private String content;
    private OffsetDateTime created;
    private OffsetDateTime modified;
}

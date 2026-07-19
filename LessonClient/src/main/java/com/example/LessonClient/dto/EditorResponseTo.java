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
public class EditorResponseTo {

    private Long id;
    private String login;
    private String firstname;
    private String lastname;
    private OffsetDateTime created;
    private OffsetDateTime modified;
}

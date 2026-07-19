package com.example.LessonRest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
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

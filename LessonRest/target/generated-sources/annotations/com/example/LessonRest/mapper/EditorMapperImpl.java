package com.example.LessonRest.mapper;

import com.example.LessonRest.dto.EditorRequestTo;
import com.example.LessonRest.dto.EditorResponseTo;
import com.example.LessonRest.entity.Editor;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-20T16:36:31+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 25.0.1 (Amazon.com Inc.)"
)
@Component
public class EditorMapperImpl implements EditorMapper {

    @Override
    public EditorResponseTo toResponseTo(Editor entity) {
        if ( entity == null ) {
            return null;
        }

        EditorResponseTo editorResponseTo = new EditorResponseTo();

        editorResponseTo.setId( entity.getId() );
        editorResponseTo.setLogin( entity.getLogin() );
        editorResponseTo.setFirstname( entity.getFirstname() );
        editorResponseTo.setLastname( entity.getLastname() );

        return editorResponseTo;
    }

    @Override
    public Editor toEntity(EditorRequestTo requestTo) {
        if ( requestTo == null ) {
            return null;
        }

        Editor editor = new Editor();

        editor.setLogin( requestTo.getLogin() );
        editor.setPassword( requestTo.getPassword() );
        editor.setFirstname( requestTo.getFirstname() );
        editor.setLastname( requestTo.getLastname() );

        return editor;
    }

    @Override
    public EditorRequestTo toRequestTo(EditorResponseTo responseTo) {
        if ( responseTo == null ) {
            return null;
        }

        EditorRequestTo editorRequestTo = new EditorRequestTo();

        editorRequestTo.setLogin( responseTo.getLogin() );
        editorRequestTo.setFirstname( responseTo.getFirstname() );
        editorRequestTo.setLastname( responseTo.getLastname() );

        return editorRequestTo;
    }
}

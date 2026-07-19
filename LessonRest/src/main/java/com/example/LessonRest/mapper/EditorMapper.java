package com.example.LessonRest.mapper;

import com.example.LessonRest.dto.EditorRequestTo;
import com.example.LessonRest.dto.EditorResponseTo;
import com.example.LessonRest.entity.Editor;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface EditorMapper {
    
    EditorMapper INSTANCE = Mappers.getMapper(EditorMapper.class);
    
    EditorResponseTo toResponseTo(Editor entity);
    
    Editor toEntity(EditorRequestTo requestTo);
    
    EditorRequestTo toRequestTo(EditorResponseTo responseTo);
}

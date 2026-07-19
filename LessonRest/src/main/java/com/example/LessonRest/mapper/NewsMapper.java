package com.example.LessonRest.mapper;

import com.example.LessonRest.dto.NewsRequestTo;
import com.example.LessonRest.dto.NewsResponseTo;
import com.example.LessonRest.entity.News;
import com.example.LessonRest.repository.EditorRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
@Component
public interface NewsMapper {
    
    NewsMapper INSTANCE = Mappers.getMapper(NewsMapper.class);
    
    // Для NewsResponseTo -> NewsRequestTo - конвертируем OffsetDateTime в String
    @Mappings({
        @Mapping(target = "created", source = "created", qualifiedByName = "offsetDateTimeToString"),
        @Mapping(target = "modified", source = "modified", qualifiedByName = "offsetDateTimeToString")
    })
    NewsRequestTo toRequestTo(NewsResponseTo responseTo);
    
    // Для toEntity - конвертируем String в LocalDateTime (editor устанавливается вручную в контроллере)
    @Mappings({
        @Mapping(target = "createdAt", source = "created", qualifiedByName = "stringToLocalDateTime"),
        @Mapping(target = "modifiedAt", source = "modified", qualifiedByName = "stringToLocalDateTime")
    })
    News toEntity(NewsRequestTo requestTo);
    
    // Для toResponseTo - конвертируем LocalDateTime в OffsetDateTime
    @Mappings({
        @Mapping(target = "created", source = "createdAt", qualifiedByName = "localDateTimeToOffsetDateTime"),
        @Mapping(target = "modified", source = "modifiedAt", qualifiedByName = "localDateTimeToOffsetDateTime"),
        @Mapping(target = "editorId", source = "editor.id")
    })
    NewsResponseTo toResponseTo(News entity);
    
    @Named("offsetDateTimeToString")
    default String offsetDateTimeToString(OffsetDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.toString();
    }
    
    @Named("stringToLocalDateTime")
    default LocalDateTime stringToLocalDateTime(String date) {
        if (date == null || date.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(date, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e) {
            try {
                // Try parsing with offset
                return LocalDateTime.parse(date);
            } catch (Exception ex) {
                return null;
            }
        }
    }
    
    @Named("localDateTimeToOffsetDateTime")
    default OffsetDateTime localDateTimeToOffsetDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.atOffset(java.time.ZoneOffset.UTC);
    }
}

package com.example.LessonRest.mapper;

import com.example.LessonRest.dto.NoticeRequestTo;
import com.example.LessonRest.dto.NoticeResponseTo;
import com.example.LessonRest.entity.Notice;
import com.example.LessonRest.entity.News;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface NoticeMapper {
    
    NoticeMapper INSTANCE = Mappers.getMapper(NoticeMapper.class);
    
    @Mapping(target = "newsId", source = "news.id")
    @Mapping(target = "newsTitle", source = "news.title")
    NoticeResponseTo toResponseTo(Notice entity);
    
    @Mapping(target = "news", ignore = true)
    Notice toEntity(NoticeRequestTo requestTo);
    
    NoticeRequestTo toRequestTo(NoticeResponseTo responseTo);
}

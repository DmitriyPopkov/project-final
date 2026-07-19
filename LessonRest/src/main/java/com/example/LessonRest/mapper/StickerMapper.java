package com.example.LessonRest.mapper;

import com.example.LessonRest.dto.StickerRequestTo;
import com.example.LessonRest.dto.StickerResponseTo;
import com.example.LessonRest.entity.Sticker;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface StickerMapper {
    
    StickerMapper INSTANCE = Mappers.getMapper(StickerMapper.class);
    
    StickerResponseTo toResponseTo(Sticker entity);
    
    Sticker toEntity(StickerRequestTo requestTo);
    
    StickerRequestTo toRequestTo(StickerResponseTo responseTo);
}

package com.example.LessonRest.mapper;

import com.example.LessonRest.dto.StickerRequestTo;
import com.example.LessonRest.dto.StickerResponseTo;
import com.example.LessonRest.entity.Sticker;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-20T16:25:11+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 25.0.1 (Amazon.com Inc.)"
)
@Component
public class StickerMapperImpl implements StickerMapper {

    @Override
    public StickerResponseTo toResponseTo(Sticker entity) {
        if ( entity == null ) {
            return null;
        }

        StickerResponseTo stickerResponseTo = new StickerResponseTo();

        stickerResponseTo.setId( entity.getId() );
        stickerResponseTo.setName( entity.getName() );
        stickerResponseTo.setColor( entity.getColor() );

        return stickerResponseTo;
    }

    @Override
    public Sticker toEntity(StickerRequestTo requestTo) {
        if ( requestTo == null ) {
            return null;
        }

        Sticker sticker = new Sticker();

        sticker.setName( requestTo.getName() );
        sticker.setColor( requestTo.getColor() );

        return sticker;
    }

    @Override
    public StickerRequestTo toRequestTo(StickerResponseTo responseTo) {
        if ( responseTo == null ) {
            return null;
        }

        StickerRequestTo stickerRequestTo = new StickerRequestTo();

        stickerRequestTo.setName( responseTo.getName() );
        stickerRequestTo.setDescription( responseTo.getDescription() );
        stickerRequestTo.setColor( responseTo.getColor() );

        return stickerRequestTo;
    }
}

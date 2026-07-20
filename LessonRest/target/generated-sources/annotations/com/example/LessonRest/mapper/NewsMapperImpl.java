package com.example.LessonRest.mapper;

import com.example.LessonRest.dto.NewsRequestTo;
import com.example.LessonRest.dto.NewsResponseTo;
import com.example.LessonRest.entity.Editor;
import com.example.LessonRest.entity.News;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-20T14:56:38+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 25.0.1 (Amazon.com Inc.)"
)
@Component
public class NewsMapperImpl implements NewsMapper {

    @Override
    public NewsRequestTo toRequestTo(NewsResponseTo responseTo) {
        if ( responseTo == null ) {
            return null;
        }

        NewsRequestTo newsRequestTo = new NewsRequestTo();

        newsRequestTo.setCreated( offsetDateTimeToString( responseTo.getCreated() ) );
        newsRequestTo.setModified( offsetDateTimeToString( responseTo.getModified() ) );
        newsRequestTo.setTitle( responseTo.getTitle() );
        newsRequestTo.setContent( responseTo.getContent() );
        newsRequestTo.setEditorId( responseTo.getEditorId() );
        List<Long> list = responseTo.getStickerIds();
        if ( list != null ) {
            newsRequestTo.setStickerIds( new ArrayList<Long>( list ) );
        }

        return newsRequestTo;
    }

    @Override
    public News toEntity(NewsRequestTo requestTo) {
        if ( requestTo == null ) {
            return null;
        }

        News news = new News();

        news.setCreatedAt( stringToLocalDateTime( requestTo.getCreated() ) );
        news.setModifiedAt( stringToLocalDateTime( requestTo.getModified() ) );
        news.setTitle( requestTo.getTitle() );
        news.setContent( requestTo.getContent() );

        return news;
    }

    @Override
    public NewsResponseTo toResponseTo(News entity) {
        if ( entity == null ) {
            return null;
        }

        NewsResponseTo newsResponseTo = new NewsResponseTo();

        newsResponseTo.setCreated( localDateTimeToOffsetDateTime( entity.getCreatedAt() ) );
        newsResponseTo.setModified( localDateTimeToOffsetDateTime( entity.getModifiedAt() ) );
        newsResponseTo.setEditorId( entityEditorId( entity ) );
        newsResponseTo.setId( entity.getId() );
        newsResponseTo.setTitle( entity.getTitle() );
        newsResponseTo.setContent( entity.getContent() );

        return newsResponseTo;
    }

    private Long entityEditorId(News news) {
        if ( news == null ) {
            return null;
        }
        Editor editor = news.getEditor();
        if ( editor == null ) {
            return null;
        }
        Long id = editor.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}

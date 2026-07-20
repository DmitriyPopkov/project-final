package com.example.LessonRest.mapper;

import com.example.LessonRest.dto.NoticeRequestTo;
import com.example.LessonRest.dto.NoticeResponseTo;
import com.example.LessonRest.entity.News;
import com.example.LessonRest.entity.Notice;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-20T16:25:11+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 25.0.1 (Amazon.com Inc.)"
)
@Component
public class NoticeMapperImpl implements NoticeMapper {

    @Override
    public NoticeResponseTo toResponseTo(Notice entity) {
        if ( entity == null ) {
            return null;
        }

        NoticeResponseTo noticeResponseTo = new NoticeResponseTo();

        noticeResponseTo.setNewsId( entityNewsId( entity ) );
        noticeResponseTo.setNewsTitle( entityNewsTitle( entity ) );
        noticeResponseTo.setId( entity.getId() );
        noticeResponseTo.setContent( entity.getContent() );

        return noticeResponseTo;
    }

    @Override
    public Notice toEntity(NoticeRequestTo requestTo) {
        if ( requestTo == null ) {
            return null;
        }

        Notice notice = new Notice();

        notice.setContent( requestTo.getContent() );

        return notice;
    }

    @Override
    public NoticeRequestTo toRequestTo(NoticeResponseTo responseTo) {
        if ( responseTo == null ) {
            return null;
        }

        NoticeRequestTo noticeRequestTo = new NoticeRequestTo();

        noticeRequestTo.setContent( responseTo.getContent() );

        return noticeRequestTo;
    }

    private Long entityNewsId(Notice notice) {
        if ( notice == null ) {
            return null;
        }
        News news = notice.getNews();
        if ( news == null ) {
            return null;
        }
        Long id = news.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String entityNewsTitle(Notice notice) {
        if ( notice == null ) {
            return null;
        }
        News news = notice.getNews();
        if ( news == null ) {
            return null;
        }
        String title = news.getTitle();
        if ( title == null ) {
            return null;
        }
        return title;
    }
}

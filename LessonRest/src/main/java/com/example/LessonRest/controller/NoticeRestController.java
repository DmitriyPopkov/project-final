package com.example.LessonRest.controller;

import com.example.LessonRest.dto.NoticeRequestTo;
import com.example.LessonRest.dto.NoticeResponseTo;
import com.example.LessonRest.entity.Notice;
import com.example.LessonRest.repository.NoticeRepository;
import com.example.LessonRest.mapper.NoticeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1.0/notices")
public class NoticeRestController {

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private NoticeMapper noticeMapper;

    @GetMapping
    public List<NoticeResponseTo> getAllNotices() {
        return noticeRepository.findAll().stream()
                .map(noticeMapper::toResponseTo)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public NoticeResponseTo getNoticeById(@PathVariable Long id) {
        return noticeRepository.findById(id)
                .map(noticeMapper::toResponseTo)
                .orElse(null);
    }

    @PostMapping
    public ResponseEntity<NoticeResponseTo> createNotice(@RequestBody NoticeRequestTo request) {
        Notice notice = noticeMapper.toEntity(request);
        Notice saved = noticeRepository.save(notice);
        NoticeResponseTo response = noticeMapper.toResponseTo(saved);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoticeResponseTo> updateNotice(@PathVariable Long id, @RequestBody NoticeRequestTo request) {
        Notice notice = noticeRepository.findById(id).orElse(null);
        if (notice != null) {
            notice.setContent(request.getContent());
            Notice saved = noticeRepository.save(notice);
            return ResponseEntity.ok(noticeMapper.toResponseTo(saved));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotice(@PathVariable Long id) {
        if (noticeRepository.existsById(id)) {
            noticeRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

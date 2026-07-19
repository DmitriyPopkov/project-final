package com.example.LessonClient.service;

import com.example.LessonClient.dto.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class ApiClient {

    private final WebClient webClient;

    public ApiClient(@Value("${api.base-url:http://localhost:24110/api/v1.0}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    // Editor endpoints
    public Flux<EditorResponseTo> getAllEditors() {
        return webClient.get()
                .uri("/editors")
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, clientResponse -> Mono.empty())
                .bodyToFlux(EditorResponseTo.class);
    }

    public EditorResponseTo getEditorById(Long id) {
        return webClient.get()
                .uri("/editors/{id}", id)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, clientResponse -> Mono.empty())
                .bodyToMono(EditorResponseTo.class)
                .block();
    }

    public EditorResponseTo createEditor(EditorRequestTo request) {
        return webClient.post()
                .uri("/editors")
                .body(Mono.just(request), EditorRequestTo.class)
                .retrieve()
                .onStatus(HttpStatus.BAD_REQUEST::equals, clientResponse -> {
                    throw new RuntimeException("Validation error: " + clientResponse.bodyToMono(String.class).block());
                })
                .bodyToMono(EditorResponseTo.class)
                .block();
    }

    public EditorResponseTo updateEditor(Long id, EditorRequestTo request) {
        return webClient.put()
                .uri("/editors/{id}", id)
                .body(Mono.just(request), EditorRequestTo.class)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, clientResponse -> Mono.empty())
                .bodyToMono(EditorResponseTo.class)
                .block();
    }

    public void deleteEditor(Long id) {
        webClient.delete()
                .uri("/editors/{id}", id)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, clientResponse -> Mono.empty())
                .bodyToMono(Void.class)
                .block();
    }

    // News endpoints
    public Flux<NewsResponseTo> getAllNews() {
        return webClient.get()
                .uri("/news")
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, clientResponse -> Mono.empty())
                .bodyToFlux(NewsResponseTo.class);
    }

    public NewsResponseTo getNewsById(Long id) {
        return webClient.get()
                .uri("/news/{id}", id)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, clientResponse -> Mono.empty())
                .bodyToMono(NewsResponseTo.class)
                .block();
    }

    public NewsResponseTo createNews(NewsRequestTo request) {
        return webClient.post()
                .uri("/news")
                .body(Mono.just(request), NewsRequestTo.class)
                .retrieve()
                .onStatus(HttpStatus.BAD_REQUEST::equals, clientResponse -> {
                    throw new RuntimeException("Validation error: " + clientResponse.bodyToMono(String.class).block());
                })
                .bodyToMono(NewsResponseTo.class)
                .block();
    }

    public NewsResponseTo updateNews(Long id, NewsRequestTo request) {
        return webClient.put()
                .uri("/news/{id}", id)
                .body(Mono.just(request), NewsRequestTo.class)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, clientResponse -> Mono.empty())
                .bodyToMono(NewsResponseTo.class)
                .block();
    }

    public void deleteNews(Long id) {
        webClient.delete()
                .uri("/news/{id}", id)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, clientResponse -> Mono.empty())
                .bodyToMono(Void.class)
                .block();
    }

    // Sticker endpoints
    public Flux<StickerResponseTo> getAllStickers() {
        return webClient.get()
                .uri("/stickers")
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, clientResponse -> Mono.empty())
                .bodyToFlux(StickerResponseTo.class);
    }

    public StickerResponseTo getStickerById(Long id) {
        return webClient.get()
                .uri("/stickers/{id}", id)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, clientResponse -> Mono.empty())
                .bodyToMono(StickerResponseTo.class)
                .block();
    }

    public StickerResponseTo createSticker(StickerRequestTo request) {
        return webClient.post()
                .uri("/stickers")
                .body(Mono.just(request), StickerRequestTo.class)
                .retrieve()
                .onStatus(HttpStatus.BAD_REQUEST::equals, clientResponse -> {
                    throw new RuntimeException("Validation error: " + clientResponse.bodyToMono(String.class).block());
                })
                .bodyToMono(StickerResponseTo.class)
                .block();
    }

    public StickerResponseTo updateSticker(Long id, StickerRequestTo request) {
        return webClient.put()
                .uri("/stickers/{id}", id)
                .body(Mono.just(request), StickerRequestTo.class)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, clientResponse -> Mono.empty())
                .bodyToMono(StickerResponseTo.class)
                .block();
    }

    public void deleteSticker(Long id) {
        webClient.delete()
                .uri("/stickers/{id}", id)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, clientResponse -> Mono.empty())
                .bodyToMono(Void.class)
                .block();
    }

    // Notice endpoints
    public Flux<NoticeResponseTo> getAllNotices() {
        return webClient.get()
                .uri("/notices")
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, clientResponse -> Mono.empty())
                .bodyToFlux(NoticeResponseTo.class);
    }

    public NoticeResponseTo getNoticeById(Long id) {
        return webClient.get()
                .uri("/notices/{id}", id)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, clientResponse -> Mono.empty())
                .bodyToMono(NoticeResponseTo.class)
                .block();
    }

    public NoticeResponseTo createNotice(NoticeRequestTo request) {
        return webClient.post()
                .uri("/notices")
                .body(Mono.just(request), NoticeRequestTo.class)
                .retrieve()
                .onStatus(HttpStatus.BAD_REQUEST::equals, clientResponse -> {
                    throw new RuntimeException("Validation error: " + clientResponse.bodyToMono(String.class).block());
                })
                .bodyToMono(NoticeResponseTo.class)
                .block();
    }

    public NoticeResponseTo updateNotice(Long id, NoticeRequestTo request) {
        return webClient.put()
                .uri("/notices/{id}", id)
                .body(Mono.just(request), NoticeRequestTo.class)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, clientResponse -> Mono.empty())
                .bodyToMono(NoticeResponseTo.class)
                .block();
    }

    public void deleteNotice(Long id) {
        webClient.delete()
                .uri("/notices/{id}", id)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, clientResponse -> Mono.empty())
                .bodyToMono(Void.class)
                .block();
    }

    // Authentication endpoints - для получения текущего редактора
    public ProfileResponseTo getCurrentProfile() {
        try {
            return webClient.get()
                    .uri("/auth/profile")
                    .retrieve()
                    .onStatus(HttpStatus.UNAUTHORIZED::equals, clientResponse -> Mono.empty())
                    .bodyToMono(ProfileResponseTo.class)
                    .block();
        } catch (Exception e) {
            return null;
        }
    }
}

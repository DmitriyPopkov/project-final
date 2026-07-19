package com.example.LessonRest.repository;

import java.util.List;
import java.util.Optional;

/**
 * Обобщённый интерфейс для операций CRUD.
 * @param <T> тип сущности
 */
public interface BaseRepository<T> {

    /**
     * Сохраняет сущность. Если у сущности нет id, присваивает новый.
     * @param entity сущность
     * @return сохранённая сущность
     */
    T save(T entity);

    /**
     * Возвращает сущность по id.
     * @param id идентификатор
     * @return Optional с сущностью
     */
    Optional<T> findById(Long id);

    /**
     * Возвращает все сущности.
     * @return список сущностей
     */
    List<T> findAll();

    /**
     * Удаляет сущность по id.
     * @param id идентификатор
     * @return true, если сущность была удалена, иначе false
     */
    boolean deleteById(Long id);
}

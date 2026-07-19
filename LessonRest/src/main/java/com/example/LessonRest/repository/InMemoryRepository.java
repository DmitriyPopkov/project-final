package com.example.LessonRest.repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory реализация репозитория на основе ConcurrentHashMap.
 * @param <T> тип сущности
 */
public class InMemoryRepository<T> implements BaseRepository<T> {

    private final Map<Long, T> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    private final Map<String, Object> entityMetadata = new HashMap<>();

    @Override
    public T save(T entity) {
        try {
            var idField = entity.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            Long id = (Long) idField.get(entity);
            if (id == null) {
                id = idGenerator.getAndIncrement();
                idField.set(entity, id);
            }
            storage.put(id, entity);
            return entity;
        } catch (Exception e) {
            throw new RuntimeException("Failed to save entity", e);
        }
    }

    @Override
    public Optional<T> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public boolean deleteById(Long id) {
        return storage.remove(id) != null;
    }

    public int size() {
        return storage.size();
    }

    public void clear() {
        storage.clear();
    }
}
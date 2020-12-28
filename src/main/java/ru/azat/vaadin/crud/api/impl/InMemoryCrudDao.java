package ru.azat.vaadin.crud.api.impl;

import ru.azat.vaadin.crud.api.CrudDao;

import java.util.List;
import java.util.stream.Stream;

public abstract class InMemoryCrudDao<T> implements CrudDao<T> {

    private List<T> storage;

    public InMemoryCrudDao(List<T> storage) {
        this.storage = storage;
    }

    @Override
    public T create(T element) {
        return update(element);
    }

    @Override
    public T update(T element) {
        if(!storage.contains(element)) {
            storage.add(element);
        }
        return element;
    }

    @Override
    public void delete(T element) {
        storage.remove(element);
    }

    @Override
    public Stream<T> readAll() {
        return storage.stream();
    }

    public List<T> getAll() {
        return storage;
    }

    public List<T> updateAll(List<T> newList) {
        storage = newList;
        return storage;
    }

    @Override
    public int count() {
        return storage.size();
    }

    @Override
    public Stream<T> load(int offset, int limit) {
        return storage.stream().skip(offset).limit(limit);
    }
}

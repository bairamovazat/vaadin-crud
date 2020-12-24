package ru.azat.vaadin.crud.api.impl;

import ru.azat.vaadin.crud.api.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ArrayQuery<T> implements Query<Predicate<T>> {

    private final List<Predicate<T>> predicates = new ArrayList<>();

    @Override
    public void addFilter(List<Predicate<T>> filters) {
        predicates.addAll(filters);
    }

    @Override
    public void addFilter(Predicate<T> filter) {
        predicates.add(filter);
    }

    @Override
    public void clearFilters() {
        predicates.clear();
    }

    @Override
    public List<Predicate<T>> getFilters() {
        return predicates;
    }
}

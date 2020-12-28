package ru.azat.vaadin.crud.api.impl;

import ru.azat.vaadin.crud.api.Query;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public class VoidQuery<T> implements Query<Void> {

    @Override
    public void addFilter(List<Void> filters) {

    }

    @Override
    public void addFilter(Void filter) {

    }

    @Override
    public void clearFilters() {

    }

    @Override
    public void removeFilter(Void filter) {

    }

    @Override
    public List<Void> getFilters() {
        return new ArrayList<>();
    }
}

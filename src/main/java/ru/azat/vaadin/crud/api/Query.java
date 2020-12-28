package ru.azat.vaadin.crud.api;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

/**
 * @param <F> - Тип фильтра
 */
public interface Query<F> {

    void addFilter(List<F> filters);

    void addFilter(F filter);

    void clearFilters();

    void removeFilter(F filter);

    List<F> getFilters();
}

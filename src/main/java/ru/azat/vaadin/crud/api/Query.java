package ru.azat.vaadin.crud.api;

import java.util.List;

/**
 * @param <F> - Тип фильтра
 */
public interface Query<F> {

    void addFilter(List<F> filters);

    void addFilter(F filter);

    void clearFilters();

    List<F> getFilters();
}

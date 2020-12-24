package ru.azat.vaadin.crud.api;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Crud операции с возможностью подгрузки данных
 * @param <T> - тип данных
 * @param <Q> - объект для фильтрации и тд
 */
public interface FilteringCrudDao<T, F, Q extends Query<F> > extends CrudDao<T>{

    Stream<T> load(int offset, int limit, Optional<Q> filter);

    int count(Optional<Q> filter);

}

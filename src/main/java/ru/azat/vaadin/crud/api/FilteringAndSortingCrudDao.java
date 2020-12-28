package ru.azat.vaadin.crud.api;

import com.vaadin.flow.data.provider.QuerySortOrder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Crud операции с возможностью подгрузки данных
 * @param <T> - тип данных
 * @param <F> - Фильтр
 * @param <S> - Сортировка
 * @param <Q> - объект для фильтрации и тд
 */
public interface FilteringAndSortingCrudDao<T, F, Q extends Query<F>> extends CrudDao<T>{

    Stream<T> load(int offset, int limit, Optional<Q> filter, List<QuerySortOrder> querySortOrders);

    int count(Optional<Q> filter);

}

package ru.azat.vaadin.crud.common;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.function.ValueProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.function.Function;

/**
 * @param <T> - Тип
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ColumnDefinition<T, F> {

    private ValueProvider<T, ?> getter;

    private Function<Binder<T>, ? extends Component> bind;

    private Function<String, F> filter;

    private QuerySortOrder order;

    private Renderer<T> renderer;

    private String columnName;

    private boolean sortable;

    private String sortProperty;

    @Builder.Default
    private boolean visible = true;

    @Builder.Default
    private boolean editable = false;

}

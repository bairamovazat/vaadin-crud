package ru.azat.vaadin.crud.common;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.dialog.Dialog;
import lombok.Getter;
import ru.azat.vaadin.crud.api.impl.ArrayQuery;
import ru.azat.vaadin.crud.api.impl.InMemoryCrudDao;


import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class DialogGrid<T> extends CustomField<List<T>> {

    @Getter
    private CrudGrid<T, Predicate<T>> grid;

    private Dialog dialog;

    private InMemoryCrudDao<T> crudDao = createCrudData(new ArrayList<>());

    private final List<ColumnDefinition<T, Predicate<T>>> columnDefinitions;

    private final Supplier<T> createNewInstance;

    public DialogGrid(List<ColumnDefinition<T, Predicate<T>>> columnDefinitions, Supplier<T> createNewInstance) {
        this.createNewInstance = createNewInstance;
        this.columnDefinitions = columnDefinitions;
        init("Изменить");
    }

    public DialogGrid(String buttonName, List<ColumnDefinition<T, Predicate<T>>> columnDefinitions, Supplier<T> createNewInstance) {
        this.createNewInstance = createNewInstance;
        this.columnDefinitions = columnDefinitions;
        init(buttonName);
    }

    @Override
    protected List<T> generateModelValue() {
        return crudDao.getAll();
    }

    @Override
    protected void setPresentationValue(List<T> t) {
        bindData(t);
    }

    private void init(String buttonName) {
        dialog = new Dialog();
        dialog.setMinWidth("800px");
        Button button = new Button(buttonName);
        button.addClickListener(event -> dialog.open());

        grid = new CrudGrid<>(crudDao, columnDefinitions, new ArrayQuery<T>());
        dialog.add(grid);
        add(button);
    }

    private void bindData(List<T> data) {
        crudDao = createCrudData(data);
        grid.bindData(crudDao);
        grid.refreshAll();
    }

    private InMemoryCrudDao<T> createCrudData(List<T> data) {
        return new InMemoryCrudDao<T>(data) {
            @Override
            public T createNew() {
                return createNewInstance.get();
            }
        };

    }


}

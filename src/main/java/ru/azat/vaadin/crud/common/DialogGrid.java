package ru.azat.vaadin.crud.common;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.dialog.Dialog;
import lombok.Getter;
import ru.azat.vaadin.crud.api.impl.VoidQuery;
import ru.azat.vaadin.crud.api.impl.InMemoryCrudDao;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class DialogGrid<T> extends CustomField<List<T>> {

    @Getter
    private CrudGrid<T, Void> grid;

    private Dialog dialog;

    private InMemoryCrudDao<T> crudDao;

    private final List<ColumnDefinition<T, Void>> columnDefinitions;

    private final Supplier<T> createNewInstance;

    public DialogGrid(List<ColumnDefinition<T, Void>> columnDefinitions,
                      Supplier<T> createNewInstance) {
        this.createNewInstance = createNewInstance;
        this.columnDefinitions = columnDefinitions;
        init("Изменить");
    }

    public DialogGrid(String buttonName, List<ColumnDefinition<T, Void>> columnDefinitions,
                      Supplier<T> createNewInstance) {
        this.createNewInstance = createNewInstance;
        this.columnDefinitions = columnDefinitions;
        init(buttonName);
    }

    @Override
    public List<T> getValue() {
        return super.getValue();
    }

    @Override
    public List<T> getEmptyValue() {
        return new ArrayList<>();
    }

    @Override
    public void setValue(List<T> value) {
        super.setValue(value);
    }

    @Override
    protected List<T> generateModelValue() {
        return crudDao.getAll();
    }

    @Override
    protected void setPresentationValue(List<T> value) {
        bindData(value);
    }

    @Override
    protected boolean valueEquals(List<T> value1, List<T> value2) {
        return Objects.equals(value1, value2);
    }

    private void init(String buttonName) {
        dialog = new Dialog();
        dialog.setMinWidth("800px");
        dialog.setMinHeight("800px");
        dialog.addDialogCloseActionListener(event -> {
            if (getGrid() != null) {
                getGrid().closeEditor();
            }
            updateValue();
            dialog.close();
        });
        Button button = new Button(buttonName);
        button.addClickListener(event -> {
            dialog.open();
            updateGrid();
        });
        add(button);
        bindData(new ArrayList<>());
    }

    private void updateGrid() {
        if (getGrid() != null) {
            dialog.remove(getGrid());
        }
        grid = new CrudGrid<T, Void>(crudDao, columnDefinitions, new VoidQuery<T>());
        dialog.add(grid);
    }

    private void bindData(List<T> value) {
        crudDao = createCrudData(value);
        updateGrid();
    }

    public CrudGrid<T, Void> getGrid() {
        return grid;
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

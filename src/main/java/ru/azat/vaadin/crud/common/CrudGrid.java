package ru.azat.vaadin.crud.common;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import lombok.Getter;
import lombok.Setter;
import ru.azat.vaadin.crud.api.CrudDao;
import ru.azat.vaadin.crud.api.FilteringAndSortingCrudDao;
import ru.azat.vaadin.crud.api.Query;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;


public class CrudGrid<T, F> extends Div {

    @Setter
    @Getter
    private boolean crudEnabled = true;

    private final List<ColumnDefinition<T, F>> columnDefinitions;
    private CrudDao<T> crudDao;
    private final Grid<T> grid = new Grid<>();
    private ConfigurableFilterDataProvider<T, Void, Query<F>> filterDataProvider;
    private final List<Supplier<F>> filterFunctions = new ArrayList<>();
    private final Query<F> query;
    /**
     * Устанавливается пользователем
     */
    @Getter
    @Setter
    private List<Supplier<F>> customFilters;

    @Setter
    private Consumer<T> beforeSave = (e) -> {
    };

    @Setter
    private Consumer<T> afterSave = (e) -> {
    };

    private HeaderRow filterRow;

    public CrudGrid(CrudDao<T> crudDao, List<ColumnDefinition<T, F>> columnDefinitions,
                    Query<F> query) {
        this.columnDefinitions = columnDefinitions;
        this.customFilters = new ArrayList<>();
        this.query = query;
        init(crudDao);
    }

    public CrudGrid(CrudDao<T> crudDao, List<ColumnDefinition<T, F>> columnDefinitions,
                    Query<F> query, boolean crudEnabled) {
        this.columnDefinitions = columnDefinitions;
        this.customFilters = new ArrayList<>();
        this.query = query;
        this.crudEnabled = crudEnabled;
        init(crudDao);
    }

    public CrudGrid(CrudDao<T> crudDao, List<ColumnDefinition<T, F>> columnDefinitions,
                    List<Supplier<F>> customFilters, Query<F> query) {
        this.columnDefinitions = columnDefinitions;
        this.customFilters = customFilters;
        this.query = query;
        init(crudDao);
    }

    public CrudGrid(CrudDao<T> crudDao, List<ColumnDefinition<T, F>> columnDefinitions,
                    List<Supplier<F>> customFilters, Query<F> query, boolean crudEnabled) {
        this.columnDefinitions = columnDefinitions;
        this.customFilters = customFilters;
        this.query = query;
        this.crudEnabled = crudEnabled;
        init(crudDao);
    }

    private void init(CrudDao<T> crudDao) {
        grid.setMultiSort(false);
        grid.setHeight("100%");
        grid.setMultiSort(true);
        this.setWidth("100%");
        this.setHeight("100%");
        bindData(crudDao);
        createBinder();
        initColumns();
        if (isCrudEnabled()) {
            createEditButtons();
            createContextMenu();
        }
        add(grid);
    }

    void closeEditor() {
        grid.getEditor().cancel();
    }

    public void bindData(CrudDao<T> crudDao) {
        this.crudDao = crudDao;
        if (crudDao instanceof FilteringAndSortingCrudDao) {
            CallbackDataProvider<T, Query<F>> provider = DataProvider.fromFilteringCallbacks(
                    (q) -> ((FilteringAndSortingCrudDao<T, F, Query<F>>) crudDao).load(q.getOffset(), q.getLimit(), q.getFilter(), q.getSortOrders()),
                    q -> ((FilteringAndSortingCrudDao<T, F, Query<F>>) crudDao).count(q.getFilter())
            );
            filterDataProvider = provider.withConfigurableFilter();
            updateFilters();
            grid.setItems(filterDataProvider);
        } else {
            CallbackDataProvider<T, Void> provider = DataProvider.fromCallbacks(
                    (q) -> crudDao.load(q.getOffset(), q.getLimit()),
                    q -> crudDao.count()
            );
            grid.setItems(provider);
        }
    }

    void refreshAll() {
        grid.getDataProvider().refreshAll();
    }

    private void createColumn(ColumnDefinition<T, F> columnDefinition) {
        if (!columnDefinition.isVisible()) {
            return;
        }
        Grid.Column<T> column;
        if (columnDefinition.getRenderer() != null) {
            column = grid.addColumn(columnDefinition.getRenderer())
                    .setHeader(columnDefinition.getColumnName());
        } else {
            column = grid.addColumn((e) -> columnDefinition.getGetter().apply(e))
                    .setHeader(columnDefinition.getColumnName());

        }
        if (columnDefinition.isSortable()) {
            column.setSortProperty(columnDefinition.getSortProperty());
        }

        if (columnDefinition.getOrder() != null) {
            column.setSortOrderProvider(direction -> Stream.of(columnDefinition.getOrder()));
        }

        if (columnDefinition.getFilter() != null) {
            TextField filterField = new TextField();

            filterField.setValueChangeMode(ValueChangeMode.LAZY);
            getFilterRow().getCell(column).setComponent(filterField);
            filterField.clear();
            filterField.addValueChangeListener(e -> updateFilters());
            filterFunctions.add(() -> {
                if (!filterField.isEmpty()) {
                    return columnDefinition.getFilter().apply(filterField.getValue());
                } else {
                    return null;
                }
            });
        }
        if (columnDefinition.isEditable() && columnDefinition.getBind() != null) {
            Component component = columnDefinition.getBind().apply(grid.getEditor().getBinder());
            column.setEditorComponent(component);

            component.getElement()
                    .addEventListener("keydown", event -> grid.getEditor().cancel())
                    .setFilter("event.key === 'Tab' && event.shiftKey");

        }
    }

    private void updateFilters() {
        List<F> filterList = new ArrayList<>();

        filterFunctions.forEach(filterFunction -> {
            F filter = filterFunction.get();
            if (filter != null) {
                filterList.add(filter);
            }
        });
        customFilters.forEach(filterFunction -> {
            F filter = filterFunction.get();
            if (filter != null) {
                filterList.add(filter);
            }
        });

        query.clearFilters();
        query.addFilter(filterList);

        filterDataProvider.setFilter(query);
    }


    private void createBinder() {
        Binder<T> binder = new Binder<>();
        grid.getEditor().setBinder(binder);
        grid.getEditor().setBuffered(true);
    }

    private void initColumns() {
        HeaderRow nameHeader = grid.appendHeaderRow();
        this.columnDefinitions.forEach(this::createColumn);
    }

    private void createEditButtons() {
        Div validationStatus = new Div();
        validationStatus.setId("validation");
        Collection<Button> editButtons = Collections
                .newSetFromMap(new WeakHashMap<>());

        Editor<T> editor = grid.getEditor();

        Grid.Column<T> editorColumn = grid.addComponentColumn(item -> {
            Button edit = new Button("Редактировать");
            edit.addClassName("edit");
            edit.addClickListener(e -> {
                editor.editItem(item);
                grid.select(item);
            });
            edit.setEnabled(!editor.isOpen());
            editButtons.add(edit);
            return edit;
        });

        editor.addOpenListener(e -> editButtons
                .forEach(button -> button.setEnabled(!editor.isOpen())));
        editor.addCloseListener(e -> editButtons
                .forEach(button -> button.setEnabled(!editor.isOpen())));

        Button save = new Button("Сохранить", e -> editor.save());
        save.addClassName("save");

        editor.addSaveListener(e -> {
            this.crudDao.update(e.getItem());
        });

        Button cancel = new Button("Отмена", e -> editor.cancel());
        cancel.addClassName("cancel");

        Div buttons = new Div(save, cancel);
        buttons.add();
        editorColumn.setEditorComponent(buttons);
    }

    private void createContextMenu() {
        GridContextMenu<T> contextMenu = new GridContextMenu<>(grid);

        contextMenu.addItem("Добавить", event -> createNewRow());

        contextMenu.addItem("Изменить",
                event -> event.getItem().ifPresent(this::editItem));

        contextMenu.addItem("Удалить",
                event -> event.getItem().ifPresent(this::delete));
    }

    private void editItem(T element) {
        grid.getEditor().editItem(element);
    }

    private void createNewRow() {
        grid.getEditor().cancel();
        CrudForm<T, F> crudForm = new CrudForm<>(columnDefinitions, crudDao.createNew());
        crudForm.setSaveListener(this::save);
        crudForm.open();
    }

    private void save(T element) {
        beforeSave.accept(element);
        this.crudDao.update(element);
        afterSave.accept(element);
        grid.getDataProvider().refreshAll();
    }

    private void delete(T element) {
        this.crudDao.delete(element);
        grid.getDataProvider().refreshAll();
    }

    private boolean isCrudEnabled() {
        return crudEnabled && crudDao != null;
    }

    private HeaderRow getFilterRow() {
        if (filterRow == null) {
            filterRow = grid.appendHeaderRow();
        }
        return filterRow;
    }
}

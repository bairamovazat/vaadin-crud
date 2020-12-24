package ru.azat.vaadin.crud.common;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.Binder;
import lombok.Setter;

import java.util.List;
import java.util.function.Consumer;

public class CrudForm<T, F> extends Dialog {

    private final List<ColumnDefinition<T, F>> columnDefinitions;
    private final T writeObject;
    @Setter
    private Consumer<T> saveListener;

    public CrudForm(List<ColumnDefinition<T, F>> columnDefinitions, T writeObject) {
        this.columnDefinitions = columnDefinitions;
        this.writeObject = writeObject;
        initDialog();
    }

    private void initDialog() {
        FormLayout formLayout = new FormLayout();
        Binder<T> binder = new Binder<T>();
        this.columnDefinitions
                .stream().filter(e -> e.getBind() != null)
                .forEach(e -> {
                    Component component = e.getBind().apply(binder);
                    component.setVisible(e.isVisible());
                    component.onEnabledStateChanged(e.isEditable());
                    formLayout.addFormItem(component, e.getColumnName());
                });
        Button save = new Button("Сохранить");
        Button reset = new Button("Отмена");

        save.addClickListener(buttonClickEvent -> {
            if (binder.writeBeanIfValid(writeObject)) {
                saveListener.accept(writeObject);
                this.close();
            }
        });

        reset.addClickListener(buttonClickEvent -> {
            this.close();
        });

        HorizontalLayout actions = new HorizontalLayout();
        actions.add(save, reset);
        save.getStyle().set("marginRight", "10px");

        add(formLayout, actions);
    }

}

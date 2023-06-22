package com.rihalChallenge.application.views.data;

import com.rihalChallenge.application.databaseControl.ClassesDatabaseController;
import com.rihalChallenge.application.structures.Class;
import com.rihalChallenge.application.views.layouts.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.LinkedList;
import java.util.List;

@PageTitle("Rihal Challenge | Omar Al-Suleimani")
@Route(value = "Classes", layout = MainLayout.class)
public class ClassesView extends VerticalLayout {
    private Grid<Class> grid = new Grid<>(Class.class);
    private TextField filter = new TextField();
    private FormLayout form = new FormLayout();
    private TextField tfId = new TextField("Class ID");
    private TextField tfName = new TextField("Name");
    private ClassesDatabaseController sqlite;
    private Class course;

    public ClassesView(){
        addClassName("classes-view");
        setSizeFull();
        sqlite = new ClassesDatabaseController("rihalChallenge.db");
        configureGrid();
        configureForm();
        add(getToolbar(), getContent());
        closeForm();
    }

    private void configureForm() {
        form.setWidth("25em");

        Button save = new Button("Save");
        Button delete = new Button("Delete");
        Button cancel = new Button("Cancel");

        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickListener(event -> saveClass());
        delete.addClickListener(event -> deleteClass());
        cancel.addClickListener(event -> closeForm());

        save.addClickShortcut(Key.ENTER);
        cancel.addClickShortcut(Key.ESCAPE);

        HorizontalLayout horizontalLayout = new HorizontalLayout(save, delete, cancel);
        form.add(tfId, tfName, horizontalLayout);
    }

    private void saveClass() {
        if (!sqlite.connect()){
            Notification notification = Notification.show("Failed to connect to the database!", 5000, Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
        String response;
        if (tfId.getParent().isPresent()) {
            response = sqlite.updateClass(course.getId(), tfId.getValue(), tfName.getValue());
        }
        else {
            response = sqlite.createClass(tfName.getValue());
        }
        Notification notification = Notification.show(response, 5000, Notification.Position.TOP_CENTER);
        if (response.contains("Failed") || response.contains("Invalid")) {
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }
        else {
            notification.removeThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        }
        sqlite.close();
        updateList();
        closeForm();
    }

    private void closeForm() {
        course = null;
        grid.asSingleSelect().clear();
        form.setVisible(false);
        form.getChildren().forEach(component -> {if (component instanceof TextField textField) textField.setValue("");});
    }

    private void deleteClass() {
        if (!tfId.getParent().isPresent()){
            Notification.show("Select a class from the list to delete from the database!", 5000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }
        if (!sqlite.connect()){
            Notification.show("Failed to connect to the database!", 5000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }
        String response = sqlite.deleteClass(course.getId());
        sqlite.close();
        if (response.contains("Failed")){
            Notification.show(response, 5000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }
        Notification.show(response, 5000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        updateList();
        closeForm();
    }

    private void configureGrid() {
        grid.addClassName("class-grid");
        grid.setSizeFull();
        grid.setColumns("id", "name", "createdDate", "modifiedDate", "studentCount");
        updateList();
        grid.getColumns().forEach(column -> column.setAutoWidth(true));
        grid.asSingleSelect().addValueChangeListener(e -> {
            if (!tfId.getParent().isPresent()) form.addComponentAsFirst(tfId);
            editClass(e.getValue());
        });
    }

    private void updateList() {
        if (!sqlite.connect()){
            Notification notification = Notification.show("Failed to connect to the database!", 5000, Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }
        List<Class> classes = sqlite.findAllClasses(filter.getValue());
        sqlite.close();
        if (classes == null) {
            Notification.show("SQL Exception occurred!", 5000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
            classes = new LinkedList<>();
        }
        grid.setItems(classes);
    }

    private Component getContent() {
        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.addClassName("content");
        content.setSizeFull();
        return content;
    }

    private Component getToolbar() {
        filter.setPlaceholder("Fiter by name...");
        filter.setClearButtonVisible(true);
        filter.setValueChangeMode(ValueChangeMode.LAZY);
        filter.addValueChangeListener(e -> updateList());
        Button addClassButton = new Button("Add Class");
        addClassButton.addClickListener(e -> addClass());
        HorizontalLayout toolbar = new HorizontalLayout(filter, addClassButton);
        toolbar.setClassName("toolbar");
        return toolbar;
    }

    private void addClass() {
        grid.asSingleSelect().clear();
        if (tfId.getParent().isPresent()) form.remove(tfId);
        editClass(new Class());
    }

    private void editClass(Class course) {
        if (course == null) {
            closeForm();
        }
        else {
            this.course = course;
            tfId.setValue(course.getId() + "");
            tfName.setValue(course.getName());
            form.setVisible(true);
        }
    }
}

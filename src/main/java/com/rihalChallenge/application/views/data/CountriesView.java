package com.rihalChallenge.application.views.data;

import com.rihalChallenge.application.databaseControl.CountriesDatabaseController;
import com.rihalChallenge.application.structures.Country;
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
@Route(value = "Countries", layout = MainLayout.class)
public class CountriesView extends VerticalLayout {
    private Grid<Country> grid = new Grid<>(Country.class);
    private TextField filter = new TextField();
    private FormLayout form = new FormLayout();
    private TextField tfId = new TextField("Country ID");
    private TextField tfName = new TextField("Name");
    private CountriesDatabaseController sqlite;
    private Country country;

    public CountriesView(){
        addClassName("countries-view");
        setSizeFull();
        sqlite = new CountriesDatabaseController("rihalChallenge.db");
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

        save.addClickListener(event -> saveCountry());
        delete.addClickListener(event -> deleteCountry());
        cancel.addClickListener(event -> closeForm());

        save.addClickShortcut(Key.ENTER);
        cancel.addClickShortcut(Key.ESCAPE);

        HorizontalLayout horizontalLayout = new HorizontalLayout(save, delete, cancel);
        form.add(tfId, tfName, horizontalLayout);
    }

    private void saveCountry() {
        if (!sqlite.connect()){
            Notification notification = Notification.show("Failed to connect to the database!", 5000, Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
        String response;
        if (tfId.getParent().isPresent()) {
            response = sqlite.updateCountry(country.getId() + "" , tfId.getValue(), tfName.getValue());
        }
        else {
            response = sqlite.createCountry(tfName.getValue());
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
        country = null;
        grid.asSingleSelect().clear();
        form.setVisible(false);
        form.getChildren().forEach(component -> {if (component instanceof TextField textField) textField.setValue("");});
    }

    private void deleteCountry() {
        if (!tfId.getParent().isPresent()){
            Notification.show("Select a country from the list to delete from the database!", 5000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }
        if (!sqlite.connect()){
            Notification.show("Failed to connect to the database!", 5000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }
        String response = sqlite.deleteCountry(country.getId());
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
        grid.addClassName("country-grid");
        grid.setSizeFull();
        grid.setColumns("id", "name", "createdDate", "modifiedDate", "studentCount");
        updateList();
        grid.getColumns().forEach(column -> column.setAutoWidth(true));
        grid.asSingleSelect().addValueChangeListener(e -> {
            if (!tfId.getParent().isPresent()) form.addComponentAsFirst(tfId);
            editCountry(e.getValue());
        });
    }

    private void updateList() {
        if (!sqlite.connect()){
            Notification notification = Notification.show("Failed to connect to the database!", 5000, Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }
        List<Country> countries = sqlite.findAllCountries(filter.getValue());
        sqlite.close();
        if (countries == null) {
            Notification.show("SQL Exception occurred!", 5000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
            countries = new LinkedList<>();
        }
        grid.setItems(countries);
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
        Button addCountryButton = new Button("Add Country");
        addCountryButton.addClickListener(e -> addCountry());
        HorizontalLayout toolbar = new HorizontalLayout(filter, addCountryButton);
        toolbar.setClassName("toolbar");
        return toolbar;
    }

    private void addCountry() {
        grid.asSingleSelect().clear();
        if (tfId.getParent().isPresent()) form.remove(tfId);
        editCountry(new Country());
    }

    private void editCountry(Country country) {
        if (country == null) {
            closeForm();
        }
        else {
            this.country = country;
            tfId.setValue(country.getId() + "");
            tfName.setValue(country.getName());
            form.setVisible(true);
        }
    }
}

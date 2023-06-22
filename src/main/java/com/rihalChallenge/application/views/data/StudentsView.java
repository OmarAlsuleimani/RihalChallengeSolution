package com.rihalChallenge.application.views.data;

import com.rihalChallenge.application.databaseControl.StudentsDatabaseController;
import com.rihalChallenge.application.structures.Class;
import com.rihalChallenge.application.structures.Country;
import com.rihalChallenge.application.structures.Student;
import com.rihalChallenge.application.views.layouts.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Hr;
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
@Route(value = "", layout = MainLayout.class)
public class StudentsView extends VerticalLayout {
    private Grid<Student> grid = new Grid<>(Student.class);
    private TextField filter = new TextField();
    private FormLayout form = new FormLayout();
    private TextField tfId = new TextField("Student ID");
    private TextField tfName = new TextField("Name");
    private ComboBox<Class> cbClass = new ComboBox<>("Class");
    private ComboBox<Country> cbCountry = new ComboBox<>("Country");
    private TextField tfBirthDate = new TextField("Date of birth (yyyy-mm-dd)");
    private Text countText = new Text("");
    private Text ageText = new Text("");
    private StudentsDatabaseController sqlite;
    private Student student;

    public StudentsView(){
        addClassName("students-view");
        setSizeFull();
        sqlite = new StudentsDatabaseController("rihalChallenge.db");
        cbClass.setAllowCustomValue(false);
        cbCountry.setAllowCustomValue(false);
        configureGrid();
        configureForm();
        if (!sqlite.connect()){
            Notification.show("Database error!", 5000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }
        String count = sqlite.getStudentCount();
        String averageAge = sqlite.getAverageStudentAge();
        sqlite.close();
        if (count.contains("Failed")){
            Notification.show(count, 5000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }
        if (averageAge.contains("Failed")){
            Notification.show("Failed", 5000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }
        countText.setText("Total number of students: " + count);
        ageText.setText("Average age of students: " + averageAge);
        VerticalLayout layout = new VerticalLayout();
        layout.add(countText);
        layout.add(new Hr());
        layout.add(ageText);
        add(layout ,getToolbar(), getContent());
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

        save.addClickListener(event -> saveStudent());
        delete.addClickListener(event -> deleteStudent());
        cancel.addClickListener(event -> closeForm());

        save.addClickShortcut(Key.ENTER);
        cancel.addClickShortcut(Key.ESCAPE);

        HorizontalLayout horizontalLayout = new HorizontalLayout(save, delete, cancel);
        form.add(tfId, tfName, cbClass, cbCountry, tfBirthDate, horizontalLayout);
    }

    private void saveStudent() {
        if (!sqlite.connect()){
            Notification notification = Notification.show("Failed to connect to the database!", 5000, Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
        String response;
        if (tfId.getParent().isPresent()) {
            response = sqlite.updateStudent(student.getId() + "" , tfId.getValue(), tfName.getValue(), cbClass.getValue().getId(),
                    cbCountry.getValue().getId(), tfBirthDate.getValue().strip());
        }
        else {
            response = sqlite.createStudent(tfName.getValue(), cbClass.getValue().getId(), cbCountry.getValue().getId(), tfBirthDate.getValue());
        }
        Notification notification = Notification.show(response, 5000, Notification.Position.TOP_CENTER);
        if (response.contains("Failed") || response.contains("Invalid")) {
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            sqlite.close();
            return;
        }
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        sqlite.close();
        updateList();
        closeForm();
    }

    private void closeForm() {
        student = null;
        grid.asSingleSelect().clear();
        form.setVisible(false);
        form.getChildren().forEach(component -> {
            if (component instanceof TextField textField) textField.setValue("");
            else if (component instanceof ComboBox<?> comboBox) comboBox.setItems(new LinkedList<>());
        });
    }

    private void deleteStudent() {
        if (!tfId.getParent().isPresent()){
            Notification.show("Select a student from the list to delete from the database!", 5000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }
        if (!sqlite.connect()){
            Notification.show("Failed to connect to the database!", 5000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }
        String response = sqlite.deleteStudent(student.getId());
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
        grid.addClassName("student-grid");
        grid.setSizeFull();
        grid.setColumns("id", "name", "class_id", "country_id", "birthDate", "createdDate", "modifiedDate");
        updateList();
        grid.getColumns().forEach(column -> column.setAutoWidth(true));
        grid.asSingleSelect().addValueChangeListener(e -> {
            if (!tfId.getParent().isPresent()) form.addComponentAsFirst(tfId);
            editStudent(e.getValue());
        });
    }

    private void updateList() {
        if (!sqlite.connect()){
            Notification notification = Notification.show("Failed to connect to the database!", 5000, Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }
        String count = sqlite.getStudentCount();
        String averageAge = sqlite.getAverageStudentAge();
        List<Student> students = sqlite.findAllStudents(filter.getValue());
        sqlite.close();
        if (count.contains("Failed")){
            Notification.show(count, 5000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }
        if (averageAge.contains("Failed")){
            Notification.show("Failed", 5000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }
        countText.setText("Total number of students: " + count);
        ageText.setText("Average age of students: " + averageAge);
        if (students == null) {
            Notification.show("SQL Exception occurred!", 5000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
            students = new LinkedList<>();
        }
        grid.setItems(students);
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
        Button addStudentButton = new Button("Add Student");
        addStudentButton.addClickListener(e -> addStudent());
        HorizontalLayout toolbar = new HorizontalLayout(filter, addStudentButton);
        toolbar.setClassName("toolbar");
        return toolbar;
    }

    private void addStudent() {
        grid.asSingleSelect().clear();
        if (tfId.getParent().isPresent()) form.remove(tfId);
        editStudent(new Student());
    }

    private void editStudent(Student student) {
        if (student == null) {
            closeForm();
        }
        else {
            if (!sqlite.connect()){
                Notification.show("Failed to connect to the database!", 5000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            Class course = null;
            if (!student.getClass_id().isBlank()) {
                course = sqlite.getClassWithId(student.getClass_id());
                if (course == null) {
                    Notification.show("Database error while getting the class for this student!", 5000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
                    sqlite.close();
                    return;
                }
            }

            Country country = null;
            if (!student.getCountry_id().isBlank()){
                country = sqlite.getCountryWithId(student.getCountry_id());
                if (country == null){
                    Notification.show("Database error while getting the country for this student!", 5000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
                    sqlite.close();
                    return;
                }
            }
            //Make the lines below more efficient.
            cbCountry.setItems(sqlite.getCountriesList());
            cbCountry.setItemLabelGenerator(Country::toString);
            cbClass.setItems(sqlite.getClassesList());
            cbClass.setItemLabelGenerator(Class::toString);
            if (course != null) cbClass.setValue(course);
            if (country != null) cbCountry.setValue(country);
            sqlite.close();
            this.student = student;
            tfId.setValue(student.getId());
            tfName.setValue(student.getName());
            tfBirthDate.setValue(student.getBirthDate());
            form.setVisible(true);
        }
    }
}

package com.rihalChallenge.application.views.data;

import com.rihalChallenge.application.structures.Student;
import com.rihalChallenge.application.views.data.StudentsView;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.provider.ListDataProvider;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * The SpringBootTest class for testing UI functions.
 * This class only tests the StudentsView UI class because the other UI classes use the same algorithms in their functions.
 * Therefore, if the StudentsView class functions properly, then the other two View classes also would function properly.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class StudentsViewTest {

    @Autowired
    private StudentsView studentsView;

    /**
     * Tests that the form for updating a student entry displays with the correct information when a student is selected.
     */
    @Test
    public void formShownWhenStudentSelected(){
        Grid<Student> studentGrid = studentsView.getGrid();
        Student firstStudent = getFirstItem(studentGrid);
        FormLayout formLayout = studentsView.getForm();

        Assert.assertFalse(formLayout.isVisible());
        studentGrid.asSingleSelect().setValue(firstStudent);

        Assert.assertTrue(formLayout.isVisible());
        Assert.assertEquals(firstStudent.getName(), studentsView.getTfName().getValue());
    }

    /**
     * Tests that the form for creating a student entry displays with empty fields when the "Add Student" button is clicked.
     */
    @Test
    public void formShownCorrectlyWhenAddStudentClicked(){
        FormLayout formLayout = studentsView.getForm();

        Assert.assertFalse(formLayout.isVisible());
        studentsView.getAddStudentButton().click();

        Assert.assertTrue(formLayout.isVisible());
        Assert.assertEquals("", studentsView.getTfName().getValue());

        Assert.assertFalse(studentsView.getTfId().getParent().isPresent());
    }

    private Student getFirstItem(Grid<Student> studentGrid) {
        return ((ListDataProvider<Student>) studentGrid.getDataProvider()).getItems().iterator().next();
    }
}

package com.rihalChallenge.application.views.layouts;

import com.rihalChallenge.application.views.data.ClassesView;
import com.rihalChallenge.application.views.data.CountriesView;
import com.rihalChallenge.application.views.data.StudentsView;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;


public class MainLayout extends AppLayout {
    public MainLayout() {
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        H1 logo = new H1("Rihal Challenge | Omar Al-Suleimani");
        logo.addClassNames("text-l", "m-m");
        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(logo);
        header.setWidthFull();
        header.addClassNames("py-0", "px-m");
        addToNavbar(header);
    }

    private void createDrawer() {
        RouterLink students = new RouterLink("Students", StudentsView.class);
        students.setHighlightCondition(HighlightConditions.sameLocation());
        Button darkMode = new Button("Dark mode");
        darkMode.addClickListener(event ->{
            DarkLightModes.toggleDarkMode();
            if (DarkLightModes.isDarkMode()) darkMode.setText("Light mode");
            else darkMode.setText("Dark mode");
            updateTheme();
        });
        darkMode.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        VerticalLayout darkLayout = new VerticalLayout(darkMode);
        darkLayout.setSizeFull();
        darkLayout.getStyle().set("margin-top", "auto");
        darkLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        darkMode.getStyle().set("margin-top", "auto");
        VerticalLayout verticalLayout = new VerticalLayout(
               students,
               new RouterLink("Classes", ClassesView.class),
               new RouterLink("Countries", CountriesView.class),
               darkLayout
        );
        verticalLayout.setSizeFull();
        addToDrawer(verticalLayout);
    }

    private void updateTheme() {
        String theme = DarkLightModes.getTheme();
        getUI().ifPresent(ui -> ui.getElement().setAttribute("theme", theme));
    }
}

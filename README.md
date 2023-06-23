# Rihal Challenge Solution

This project is the solution to Rihal's web app challenge created by Omar Al-Suleimani. It satisfies all of the mandatory requirements, and 5 out of 7 bonus requirements, the missing ones being the "Generate random seed data", and "Host the app as a website and share link." requirements. 

## Project Description:
- The web app contains 3 pages, the main page: Students, and two other pages: Classes and Countries.
- Each page allows the user to perform CRUD operations on the respective tables.
- The "Add" button of each page allows the user to perform the Create operation.
- Navigating to a page automatically performs the Read operation as entries are loaded to the respective table.
- Clicking on an entry in the table allows the user to perform the Update and Delete operations for that entry.
- Deleting a class or a country would result in the deletion of all students registered to the class or the country.
- Similarly, updating the ID of a class or a country would update the respective field for every student registered to that class or country.
- IDs for all tables are generated automatically, and CreatedDate and ModifiedDate properties are created\updated automatically whenever needed. Similarly, calculations like the average age of all students and the count of students per class and country are performed automatically whenever a change is made to the database.
- The average age of students is displayed on the Students page, and the count of students per class and country are displayed for each entry in their respective table.
- The 3rd party UI library used is Vaadin.

## Running the application

The project is a standard Maven project. To run it from the command line,
type `mvnw` (Windows), or `./mvnw` (Mac & Linux), then open
http://localhost:8080 (port can be changed in application.properties file) in your browser.

You can also import the project to your IDE of choice as you would with any
Maven project. Read more on [how to import Vaadin projects to different IDEs](https://vaadin.com/docs/latest/guide/step-by-step/importing) (Eclipse, IntelliJ IDEA, NetBeans, and VS Code).

## Deploying to Production

To create a production build, call `mvnw clean package -Pproduction` (Windows),
or `./mvnw clean package -Pproduction` (Mac & Linux).
This will build a JAR file with all the dependencies and front-end resources,
ready to be deployed. The file can be found in the `target` folder after the build completes.

Once the JAR file is built, you can run it using
`java -jar target/rihalChallenge-1.0-SNAPSHOT.jar`

## Project structure

- `MainLayout.java` in `src/main/java` contains the navigation setup (i.e., the
  side/top bar and the main menu). This setup uses
  [App Layout](https://vaadin.com/docs/components/app-layout).
- `views` package in `src/main/java` contains the server-side Java views of the application.
- `views` folder in `frontend/` contains the client-side JavaScript views of the application.
- `themes` folder in `frontend/` contains the custom CSS styles.

package com.rihalChallenge.application.databaseControl;

import com.rihalChallenge.application.structures.Class;
import com.rihalChallenge.application.structures.Country;
import com.rihalChallenge.application.structures.Student;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class StudentsDatabaseController {
    private final String fileName;
    private Connection connection;

    public StudentsDatabaseController(String fileName) {
        this.fileName = fileName;
    }

    public boolean connect(){

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:src/main/resources/" + fileName);
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean close(){
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public String createStudent(String name, String class_id, String country_id, String birthDate) {
        Statement statement;
        try{
            Integer.parseInt(class_id);
            Integer.parseInt(country_id);
            LocalDate.parse(birthDate);
            statement = connection.createStatement();
        }
        catch (NumberFormatException e){
            return "Invalid id format!";
        }
        catch (DateTimeParseException e){
            e.printStackTrace();
            return "Invalid date format!";
        }
        catch (SQLException e){
            return "Failed to connect to the database!";
        }

        try {
            statement.executeUpdate("insert into students (name, class_id, country_id, birthDate, CreatedDate, ModifiedDate) " +
                    "values ('" + name + "','" + class_id + "','" + country_id + "','" + birthDate + "','" + LocalDate.now().toString() + "','" + LocalDate.now().toString() + "');");
        }
        catch (SQLException e){
            e.printStackTrace();
            return "Failed to add student to the database!";
        }
        finally {
            try {
                statement.close();
            }catch (SQLException ignored){}
        }
        return "Student inserted successfully!";
    }

    public String updateStudent(String oldId,String newId, String name, String class_id, String country_id, String birthDate) {
        Statement statement;
        try{
            Integer.parseInt(newId);
            Integer.parseInt(class_id);
            Integer.parseInt(country_id);
            LocalDate.parse(birthDate);
            statement = connection.createStatement();
        }
        catch (NumberFormatException e){
            return "Invalid id format!";
        }
        catch (DateTimeParseException e){
            e.printStackTrace();
            return "Invalid date format!";
        }
        catch (SQLException e){
            return "Failed to connect to the database!";
        }

        try {
            ResultSet resultSet = statement.executeQuery("select * from classes where id = '" + class_id + "';");
            if (!resultSet.next()) return "Failed to update student as class with id: " + class_id + " does not exist";
            resultSet = statement.executeQuery("select * from countries where id = '" + country_id + "';");
            if (!resultSet.next()) return "Failed to update student as country with id: " + country_id + " does not exist";
            resultSet = statement.executeQuery("select * from students where id = '" + oldId + "';");
            if (resultSet.next()){
                resultSet = statement.executeQuery("select * from students where id = '" + newId + "';");
                if (resultSet.next() && !newId.equals(oldId)) return "Failed to update student as id: " + newId + " already exists!";
                statement.executeUpdate("update students set id = '" + newId + "', name = '" + name + "', class_id = '" + class_id +
                        "', country_id = '" + country_id + "', birthDate = '" + birthDate + "', ModifiedDate = '" + LocalDate.now().toString() + "' where id = '" + oldId + "';");
                return "Student updated successfully!";
            }
            else {
                return "Failed to update student as this student does not exist in the database!";
            }
        }
        catch (SQLException e){
            e.printStackTrace();
            return "Failed to save changes in the database!";
        }
        finally {
            try {
                statement.close();
            }catch (SQLException ignored){}
        }
    }

    public List<Student> findAllStudents(String filter) {
        List<Student> students = new ArrayList<>();
        Statement statement;
        try{
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from Students where lower(name) like lower('%" + filter + "%');");
            while (resultSet.next()){
                students.add(new Student(resultSet.getString(1), resultSet.getString(2), resultSet.getString(3),
                        resultSet.getString(4), resultSet.getString(5), resultSet.getString(6), resultSet.getString(7)));
            }
            statement.close();
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
        return students;
    }

    public String deleteStudent(String id) {
        Statement statement;
        try {
            statement = connection.createStatement();
        }
        catch (SQLException e){
            e.printStackTrace();
            return "Failed to connect to the database!";
        }

        try {
            ResultSet resultSet = statement.executeQuery("select * from students where id = '" + id + "';");
            if (!resultSet.next()) return "Failed to delete student because student: " + id + ", does not exist!";
            statement.executeUpdate("delete from students where id = '" + id + "'");
        }
        catch (SQLException e){
            e.printStackTrace();
            return "Failed to save changes in the database!";
        }

        return "Student deleted successfully!";
    }

    public List<Country> getCountriesList(){
        List<Country> countries = new LinkedList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from countries;");
            while (resultSet.next()){
                countries.add(new Country(resultSet.getString(1), resultSet.getString(2), 0, null, null));
            }
            statement.close();
        }
        catch (SQLException e){
            e.printStackTrace();
            return null;
        }
        return countries;
    }

    public List<Class> getClassesList() {
        List<Class> classes = new LinkedList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from classes;");
            while (resultSet.next()){
                classes.add(new Class(resultSet.getString(1), resultSet.getString(2), 0, null, null));
            }
            statement.close();
        }
        catch (SQLException e){
            e.printStackTrace();
            return null;
        }
        return classes;
    }

    public Class getClassWithId(String id){
        Class course = null;
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select name from classes where id = '" + id + "';");
            if (resultSet.next()) course = new Class(id, resultSet.getString(1), 0, null , null);
            statement.close();
        }
        catch (SQLException e){
            e.printStackTrace();
            return course;
        }
        return course;
    }

    public Country getCountryWithId(String id) {
        Country country = null;
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select name from countries where id = '" + id + "';");
            if (resultSet.next()) country = new Country(id, resultSet.getString(1), 0, null, null);
            statement.close();
        }
        catch (SQLException e){
            e.printStackTrace();
            return country;
        }
        return country;
    }

    public String getStudentCount() {
        String result = "Failed to execute query!";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select count(id) from students;");
            if (resultSet.next()) {
                result = resultSet.getString(1);
            }
            statement.close();
            return result;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return result;
        }
    }

    public String getAverageStudentAge() {
        String result = "Failed to execute query!";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT avg(strftime('%Y', 'now') - strftime('%Y', birthDate) - (strftime('%m-%d', 'now') < strftime('%m-%d', birthDate))) FROM students;");
            if (resultSet.next()) {
                result = resultSet.getString(1);
                if (result == null) result = 0 + "";
            }
            statement.close();
            return result;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return result;
        }
    }
}

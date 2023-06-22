package com.rihalChallenge.application.databaseControl;

import com.rihalChallenge.application.structures.Class;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ClassesDatabaseController {
    private final String fileName;
    private Connection connection;

    public ClassesDatabaseController(String fileName) {
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

    public String createClass(String name) {
        Statement statement;
        try{
            statement = connection.createStatement();
        }
        catch (SQLException e){
            return "Failed to connect to the database!";
        }

        try {
            statement.executeUpdate("insert into classes (name, CreatedDate, ModifiedDate) " +
                    "values ('" + name + "', '" + LocalDate.now().toString() + "', '" + LocalDate.now().toString() + "');");
        }
        catch (SQLException e){
            e.printStackTrace();
            return "Failed to add class to the database!";
        }
        finally {
            try {
                statement.close();
            }catch (SQLException ignored){}
        }
        return "Class inserted successfully!";
    }

    public String updateClass(String oldId,String newId, String name) {
        Statement statement;
        try{
            Integer.parseInt(newId);
            statement = connection.createStatement();
        }
        catch (NumberFormatException e){
            return "Invalid id format!";
        }
        catch (SQLException e){
            return "Failed to connect to the database!";
        }

        try {
            ResultSet resultSet = statement.executeQuery("select * from classes where id = '" + oldId + "';");
            if (resultSet.next()){
                resultSet = statement.executeQuery("select * from classes where id = '" + newId + "';");
                if (resultSet.next() && !newId.equals(oldId)) return "Failed to update class as id: " + newId + " already exists!";
                if (newId.equals(oldId)){
                    statement.executeUpdate("update classes set id = '" + newId + "', name = '" + name + "', ModifiedDate = '" + LocalDate.now().toString() + "' where id = '" + oldId + "';");
                }else {
                    statement.executeUpdate("update classes set id = '" + newId + "', name = '" + name + "', ModifiedDate = '" + LocalDate.now().toString() + "' where id = '" + oldId + "';\n" +
                            "update students set class_id = '" + newId + "', ModifiedDate = '" + LocalDate.now().toString() + "' where class_id = '" + oldId + "';");
                }
                return "Class updated successfully!";
            }
            else {
                return "Failed to update class as this class does not exist in the database!";
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

    public List<Class> findAllClasses(String filter) {
        List<Class> classes = new ArrayList<>();
        Statement statement;
        try{
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select c.*, count(s.id) from classes as c left join students as s on c.id = s.class_id where lower(c.name) like lower('%" + filter + "%') group by c.id;");
            while (resultSet.next()){
                classes.add(new Class(resultSet.getString(1), resultSet.getString(2), resultSet.getInt(5), resultSet.getString(3), resultSet.getString(4)));
            }
            statement.close();
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
        return classes;
    }

    public String deleteClass(String id) {
        Statement statement;
        try {
            statement = connection.createStatement();
        }
        catch (SQLException e){
            e.printStackTrace();
            return "Failed to connect to the database!";
        }

        try {
            ResultSet resultSet = statement.executeQuery("select * from classes where id = '" + id + "';");
            if (!resultSet.next()) return "Failed to delete class because class: " + id + ", does not exist!";
            statement.executeUpdate("delete from classes where id = '" + id + "';\n"+
                    "delete from students where class_id = '"+ id + "';");
        }
        catch (SQLException e){
            e.printStackTrace();
            return "Failed to save changes in the database!";
        }

        return "Class deleted successfully!";
    }
}

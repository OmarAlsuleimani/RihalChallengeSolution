package com.rihalChallenge.application.databaseControl;

import com.rihalChallenge.application.structures.Country;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class CountriesDatabaseController {
    private final String fileName;
    private Connection connection;

    public CountriesDatabaseController(String fileName) {
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

    public String createCountry(String name) {
        Statement statement;
        try{
            statement = connection.createStatement();
        }
        catch (SQLException e){
            return "Failed to connect to the database!";
        }

        try {
            statement.executeUpdate("insert into countries (name, CreatedDate, ModifiedDate) " +
                    "values ('" + name + "', '" + LocalDate.now().toString() + "', '" + LocalDate.now().toString() + "');");
        }
        catch (SQLException e){
            e.printStackTrace();
            return "Failed to add country to the database!";
        }
        finally {
            try {
                statement.close();
            }catch (SQLException ignored){}
        }
        return "Country inserted successfully!";
    }

    public String updateCountry(String oldId,String newId, String name) {
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
            ResultSet resultSet = statement.executeQuery("select * from countries where id = '" + oldId + "';");
            if (resultSet.next()){
                resultSet = statement.executeQuery("select * from countries where id = '" + newId + "';");
                if (resultSet.next() && !newId.equals(oldId)) return "Failed to update country as id: " + newId + " already exists!";
                if (newId.equals(oldId)){
                    statement.executeUpdate("update countries set id = '" + newId + "', name = '" + name + "', ModifiedDate = '" + LocalDate.now().toString() + "' where id = '" + oldId + "';");
                }else {
                    statement.executeUpdate("update countries set id = '" + newId + "', name = '" + name + "', ModifiedDate = '" + LocalDate.now().toString() + "' where id = '" + oldId + "';\n" +
                            "update students set country_id = '" + newId + "', ModifiedDate = '" + LocalDate.now().toString() + "' where country_id = '" + oldId + "';");
                }
                return "Country updated successfully!";
            }
            else {
                return "Failed to update country as this country does not exist in the database!";
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

    public List<Country> findAllCountries(String filter) {
        List<Country> countries = new ArrayList<>();
        Statement statement;
        try{
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select c.*, count(s.id) from Countries as c left join students as s on c.id = s.country_id where lower(c.name) like lower('%" + filter + "%') group by c.id;");
            while (resultSet.next()){
                countries.add(new Country(resultSet.getString(1), resultSet.getString(2), resultSet.getInt(5), resultSet.getString(3), resultSet.getString(4)));
            }
            statement.close();
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
        return countries;
    }

    public String deleteCountry(String id) {
        Statement statement;
        try {
            statement = connection.createStatement();
        }
        catch (SQLException e){
            e.printStackTrace();
            return "Failed to connect to the database!";
        }

        try {
            ResultSet resultSet = statement.executeQuery("select * from countries where id = '" + id + "';");
            if (!resultSet.next()) return "Failed to delete country because country: " + id + ", does not exist!";
            statement.executeUpdate("delete from countries where id = '" + id + "';\n"+
                    "delete from students where country_id = '"+ id + "';");
        }
        catch (SQLException e){
            e.printStackTrace();
            return "Failed to save changes in the database!";
        }

        return "Country deleted successfully!";
    }
}

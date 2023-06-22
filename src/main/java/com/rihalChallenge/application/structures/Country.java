package com.rihalChallenge.application.structures;

public class Country {
    private String country_id;
    private String name = "";
    private int studentCount;
    private String createdDate;
    private String modifiedDate;

    public Country(String country_id, String name, int studentCount, String createdDate, String modifiedDate) {
        this.country_id = country_id;
        this.name = name;
        this.studentCount = studentCount;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public Country() {
    }

    public String getId() {
        return country_id;
    }

    public void setId(String country_id) {
        this.country_id = country_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStudentCount(int studentCount) {
        this.studentCount = studentCount;
    }

    public int getStudentCount() {
        return studentCount;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    @Override
    public String toString(){
        return country_id + ". " + name;
    }
}

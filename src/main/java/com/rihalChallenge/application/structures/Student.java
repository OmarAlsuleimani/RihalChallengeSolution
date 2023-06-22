package com.rihalChallenge.application.structures;

public class Student {
    private String id ;
    private String class_id;
    private String country_id;
    private String name;
    private String birthDate;
    private String createdDate;
    private String modifiedDate;

    public Student(String id, String class_id, String country_id, String name, String birthDate, String createdDate, String modifiedDate) {
        this.id = id;
        this.class_id = class_id;
        this.country_id = country_id;
        this.name = name;
        this.birthDate = birthDate;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public Student() {
        id = "";
        class_id = "";
        country_id = "";
        name = "";
        birthDate = "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClass_id() {
        return class_id;
    }

    public void setClass_id(String class_id) {
        this.class_id = class_id;
    }

    public String getCountry_id() {
        return country_id;
    }

    public void setCountry_id(String country_id) {
        this.country_id = country_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
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
}
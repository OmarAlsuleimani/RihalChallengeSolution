package com.rihalChallenge.application.structures;

public class Class {
    private String class_id;
    private String name = "";
    private int studentCount;
    private String createdDate;
    private String modifiedDate;
    public Class(String id, String name, int studentCount, String createdDate, String modifiedDate) {
        this.class_id = id;
        this.name = name;
        this.studentCount = studentCount;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public Class() {
    }

    public String getId() {
        return class_id;
    }

    public void setId(String id) {
        this.class_id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(int studentCount) {
        this.studentCount = studentCount;
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
        return class_id + ". " + name;
    }
}

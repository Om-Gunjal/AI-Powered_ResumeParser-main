package com.example.resumeparser.model;

import java.util.List;

public class ParsedResumeData {
    private int id;
    private String firstname;
    private String lastname;
    private String email;
    private List<RoleFitness> roleFitnessList;

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<RoleFitness> getRoleFitnessList() {
        return roleFitnessList;
    }

    public void setRoleFitnessList(List<RoleFitness> roleFitnessList) {
        this.roleFitnessList = roleFitnessList;
    }
}

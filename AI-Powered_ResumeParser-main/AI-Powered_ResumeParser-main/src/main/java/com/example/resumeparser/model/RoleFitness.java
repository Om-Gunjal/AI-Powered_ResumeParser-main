package com.example.resumeparser.model;

public class RoleFitness {
    private String roleName;
    private int fitness;

    public RoleFitness(String roleName, int fitness) {
        this.roleName = roleName;
        this.fitness = fitness;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public int getFitness() {
        return fitness;
    }

    public void setFitness(int fitness) {
        this.fitness = fitness;
    }
}

package com.jcg.examples.entity;

/**
 * Created by lpimentel on 26-02-2016.
 */
public class Project {
    private String name;
    private long id;

    public Project(String name, long id) {
        this.name = name;
        this.id = id;
    }

    public Project() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return name;
    }
}

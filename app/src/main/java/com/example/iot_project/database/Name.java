package com.example.iot_project.database;

import org.bson.types.ObjectId;

public class Name {
    private ObjectId id;
    private String name;

    public Name(String name) {
        this.name = name;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

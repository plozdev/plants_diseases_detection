package com.example.plantdiseasedetection.model;

import java.io.Serializable;

public class Plant implements Serializable {
    private String type;
    private String age;
    private String growingConditions;
    private String location;
    private String imageb64;

    public Plant() {
        // Required empty constructor for Firestore
    }

    public Plant(String type, String age, String growingConditions, String location, String imageUrl) {
        this.type = type;
        this.age = age;
        this.growingConditions = growingConditions;
        this.location = location;
        this.imageb64 = imageUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGrowingConditions() {
        return growingConditions;
    }

    public void setGrowingConditions(String growingConditions) {
        this.growingConditions = growingConditions;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImageB64() {
        return imageb64;
    }

    public void setImageB64(String imageUrl) {
        this.imageb64 = imageUrl;
    }
}

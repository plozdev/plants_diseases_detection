package com.example.plantdiseasedetection.model;

public class GeminiRequest {
    private String imageUri;
    private String weather;
    private String location;

    public GeminiRequest(String imageUri, String weather, String location) {
        this.imageUri = imageUri;
        this.weather = weather;
        this.location = location;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}

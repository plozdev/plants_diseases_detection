package com.example.plantdiseasedetection.model;

public class WeatherResponse {
    private Main main;
    private Weather[] weather;

    public Main getMain() {
        return main;
    }

    public Weather[] getWeather() {
        return weather;
    }

    public static class Main {
        private double temp;

        public double getTemp() {
            return temp;
        }
    }

    public static class Weather {
        private String description;

        public String getDescription() {
            return description;
        }
    }
}

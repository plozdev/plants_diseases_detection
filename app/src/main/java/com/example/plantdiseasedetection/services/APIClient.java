package com.example.plantdiseasedetection.services;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {
    private static Retrofit retrofitWeather;
    private static Retrofit retrofitGemini;

    // Weather API (OpenWeatherMap)
    public static Retrofit getClient() {
        if (retrofitWeather == null) {
            retrofitWeather = new Retrofit.Builder()
                    .baseUrl("https://api.openweathermap.org/data/2.5/") // Weather API URL
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitWeather;
    }

    // Gemini API (Google Generative Language)
    public static Retrofit getGeminiClient() {
        if (retrofitGemini == null) {
            retrofitGemini = new Retrofit.Builder()
                    .baseUrl("https://generativelanguage.googleapis.com/v1beta2/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitGemini;
    }
}

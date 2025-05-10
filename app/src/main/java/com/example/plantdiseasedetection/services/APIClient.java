package com.example.plantdiseasedetection.services;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {
    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.openweathermap.org/data/2.5/") // Weather API URL
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getGeminiClient() {
        return new Retrofit.Builder()
                .baseUrl("https://api.genai.google.com/") // Gemini API URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}

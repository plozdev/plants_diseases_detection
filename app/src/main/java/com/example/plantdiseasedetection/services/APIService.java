package com.example.plantdiseasedetection.services;

import com.example.plantdiseasedetection.BuildConfig;
import com.example.plantdiseasedetection.model.GeminiRequest;
import com.example.plantdiseasedetection.model.GeminiResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type: application/json",
            "Authorization: Bearer " + BuildConfig.GEMINI_API_KEY
    })
    @POST("models/gemini-2.5-pro-preview-05-06:generateText")
    Call<GeminiResponse> analyzeImage(@Body GeminiRequest request);
}
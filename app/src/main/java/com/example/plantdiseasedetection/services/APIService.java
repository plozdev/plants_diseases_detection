package com.example.plantdiseasedetection.services;

import com.example.plantdiseasedetection.model.GeminiRequest;
import com.example.plantdiseasedetection.model.GeminiResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers("Content-Type: application/json")
    @POST("v1/models/gemini-2.0-flash:generateContent")
    Call<GeminiResponse> analyzeImage(@Body GeminiRequest request);
}

package com.example.plantdiseasedetection.controllers;

import android.content.Context;
import androidx.annotation.NonNull;

import com.example.plantdiseasedetection.model.GeminiRequest;
import com.example.plantdiseasedetection.model.GeminiResponse;
import com.example.plantdiseasedetection.services.APIClient;
import com.example.plantdiseasedetection.services.APIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class ScanController {
    private Context context;

    public ScanController(Context context) {
        this.context = context;
    }

    public void analyzeImage(String base64Image, String weather, String region, ScanCallback callback) {
        try {
            GeminiRequest request = getGeminiRequest(weather, region, base64Image);

            APIService apiService = APIClient.getGeminiClient().create(APIService.class);
            apiService.analyzeImage(request).enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<GeminiResponse> call, @NonNull Response<GeminiResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        callback.onSuccess(response.body().getReplyText());
                    } else {
                        callback.onFailure("Failed to analyze image. Response code: " + response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<GeminiResponse> call, @NonNull Throwable t) {
                    callback.onFailure("Error: " + t.getMessage());
                }
            });
        } catch (Exception e) {
            callback.onFailure("Error: " + e.getMessage());
        }
    }

    @NonNull
    private static GeminiRequest getGeminiRequest(String weather, String region, String base64Image) {
        String prompt = "I am a farmer in " + region + " of Vietnam. Please analyze the plant image I provided and detect any diseases it may have. Based on the current weather conditions ("
                + weather + ") and the regional characteristics of (" + region + "), please provide detailed recommendations for Integrated Pest Management (IPM), prioritizing:" +
                "\n1. Low-cost and locally available materials and methods." +
                "\n2. Immediate treatments (e.g., pesticides, pruning, biological controls)." +
                "\n3. Long-term management strategies (crop rotation, resistant varieties)." +
                "\n4. Preventive measures suitable for this region to minimize future risks." +
                "\n\nMake your response clear, concise, and directly applicable to farmers.";

        return new GeminiRequest(base64Image, prompt);
    }

    // Interface callback để xử lý kết quả
    public interface ScanCallback {
        void onSuccess(String result);
        void onFailure(String error);
    }
}

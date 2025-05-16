package com.example.plantdiseasedetection.services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.plantdiseasedetection.utils.ImageUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GeminiApiClient {
    private static final String TAG = "GeminiApiClient";

    private static final String VISION_URL = "https://generativelanguage.googleapis.com/v1beta2/models/gemini-pro-vision:generateContent";
//    private static final String VISION_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";



    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private static GeminiApiClient instance;
    private OkHttpClient client;

    private GeminiApiClient() {
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    public static synchronized GeminiApiClient getInstance() {
        if (instance == null) {
            instance = new GeminiApiClient();
        }
        return instance;
    }


    /**
     * Analyze image using Gemini API vision model
     */
    public void analyzeImage(String apiKey, String promptText, String imageBase64, GeminiResponseCallback callback) {
        try {
            /*
            JSONObject jsonBody = new JSONObject();
            JSONArray contents = new JSONArray();
            JSONObject content = new JSONObject();
            JSONArray parts = new JSONArray();

            // Add text prompt part
            JSONObject textPart = new JSONObject();
            textPart.put("text", prompt);
            parts.put(textPart);

            // Add image part
            if (imageBase64 != null && !imageBase64.isEmpty()) {
                JSONObject imagePart = new JSONObject();
                JSONObject inlineData = new JSONObject(); // Đổi tên biến cho rõ ràng và đúng chuẩn API

                // Loại bỏ tiền tố nếu có
                String pureBase64Image = imageBase64;
                if (imageBase64.startsWith("data:image/jpeg;base64,")) {
                    pureBase64Image = imageBase64.substring("data:image/jpeg;base64,".length());
                } else if (imageBase64.startsWith("data:image/png;base64,")) { // Hỗ trợ thêm PNG
                    pureBase64Image = imageBase64.substring("data:image/png;base64,".length());
                }
                // Bạn có thể thêm các kiểu MIME khác nếu cần

                inlineData.put("mimeType", "image/jpeg"); // Hoặc "image/png" tùy thuộc vào ảnh của bạn
                inlineData.put("data", pureBase64Image);    // Sử dụng chuỗi Base64 thuần túy

                imagePart.put("inlineData", inlineData); // Key là "inlineData"
                parts.put(imagePart);
            }

            content.put("parts", parts);
            contents.put(content);

            jsonBody.put("contents", contents);
            jsonBody.put("generationConfig", new JSONObject()
                    .put("temperature", 0.4)
                    .put("topP", 0.95)
                    .put("topK", 32)
                    .put("maxOutputTokens", 2048));

             */
            JSONObject jsonBody = new JSONObject();
            JSONObject prompt = new JSONObject();

// Add text prompt
            prompt.put("text", promptText);

// Add image if available
            if (imageBase64 != null && !imageBase64.isEmpty()) {
                JSONObject image = new JSONObject();
                String pureBase64Image = imageBase64;

                // Remove prefix if present
                if (imageBase64.startsWith("data:image/jpeg;base64,")) {
                    pureBase64Image = imageBase64.substring("data:image/jpeg;base64,".length());
                } else if (imageBase64.startsWith("data:image/png;base64,")) {
                    pureBase64Image = imageBase64.substring("data:image/png;base64,".length());
                }

                image.put("mimeType", "image/jpeg"); // Or "image/png" depending on your image type
                image.put("data", pureBase64Image);
                prompt.put("image", image);
            }

// Add prompt to jsonBody
            jsonBody.put("prompt", prompt);

// Add generationConfig
            jsonBody.put("generationConfig", new JSONObject()
                    .put("temperature", 0.4)
                    .put("topP", 0.95)
                    .put("topK", 32)
                    .put("maxOutputTokens", 2048));

            Log.d(TAG, "Request Body: " + jsonBody.toString());
            String url = VISION_URL + "?key=" + apiKey;
            RequestBody body = RequestBody.create(JSON, jsonBody.toString());
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call,@NonNull  IOException e) {
                    Log.e(TAG, "API call failed", e);
                    callback.onFailure(e);
                }

                @Override
                public void onResponse(@NonNull Call call,@NonNull  Response response) throws IOException {
                    try {
                        if (!response.isSuccessful()) {
                            Log.d(TAG, "Unexpected response code: " + response.code());
                            throw new IOException("Unexpected response code: " + response.code());

                        }
                        Log.i(TAG, "Response: " + response.body().string());
                        String responseBody = response.body().string();
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        GeminiResponse geminiResponse = parseGeminiResponse(jsonResponse);
                        callback.onSuccess(geminiResponse);
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing response", e);
                        callback.onFailure(e);
                    }
                }
            });
        } catch (JSONException e) {
            Log.e(TAG, "Error creating request body", e);
            callback.onFailure(e);
        }
    }

    private GeminiResponse parseGeminiResponse(JSONObject jsonResponse) throws JSONException {
        StringBuilder text = new StringBuilder();

        if (jsonResponse.has("candidates")) {
            JSONArray candidates = jsonResponse.getJSONArray("candidates");
            if (candidates.length() > 0) {
                JSONObject candidate = candidates.getJSONObject(0);
                JSONObject content = candidate.getJSONObject("content");
                JSONArray parts = content.getJSONArray("parts");

                for (int i = 0; i < parts.length(); i++) {
                    JSONObject part = parts.getJSONObject(i);
                    if (part.has("text")) {
                        text.append(part.getString("text"));
                    }
                }
            }
        }

        return new GeminiResponse(text.toString());
    }

    public static class GeminiResponse {
        private String text;

        public GeminiResponse(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }

    public interface GeminiResponseCallback {
        void onSuccess(GeminiResponse response);
        void onFailure(Throwable t);
    }
}

package com.example.plantdiseasedetection.controllers;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
public class ScanController {
    private Context context;

    public ScanController(Context context) {
        this.context = context;
    }

    public void processImage(Uri imageUri) {
        //TODO: gửi ảnh đến server với vị trí và thời tiết

        //Gọi API that hoặc thay bằng Retrofit

        Log.d("ScanController", "Đang xử lý ảnh: " + imageUri.toString());

        // Ví dụ kết quả trả về
        String fakeResult = "Detected: Powdery mildew (nấm mốc trắng)";

//        Toast.makeText(context, fakeResult, Toast.LENGTH_LONG).show();
    }

    public void processCapturedImage(Bitmap bitmap) {
        // TODO: Tương tự với ảnh từ camera (bitmap)
        Log.d("ScanController", "Đang xử lý ảnh chụp từ camera...");
//        Toast.makeText(context, "Ảnh đã được gửi xử lý...", Toast.LENGTH_SHORT).show();
    }

    private void analyzeImage(Uri imageUri, String weather, String location, ScanCallback callback) {

    }

    // Interface callback để xử lý kết quả
    public interface ScanCallback {
        void onSuccess(String result);
        void onFailure(String error);
    }
}

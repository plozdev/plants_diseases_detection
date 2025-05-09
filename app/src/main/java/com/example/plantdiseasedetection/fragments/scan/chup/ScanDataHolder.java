package com.example.plantdiseasedetection.fragments.scan.chup;

import android.graphics.Bitmap;

public class ScanDataHolder {
    private static Bitmap bitmap;
    private static String resultText; // Kết quả phân tích

    public static void setBitmap(Bitmap b) {
        bitmap = b;
    }

    public static Bitmap getBitmap() {
        return bitmap;
    }

    public static void setResultText(String text) {
        resultText = text;
    }

    public static String getResultText() {
        return resultText;
    }

    public static void clear() {
        bitmap = null;
        resultText = null;
    }
}
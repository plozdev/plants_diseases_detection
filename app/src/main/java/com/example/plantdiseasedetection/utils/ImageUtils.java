package com.example.plantdiseasedetection.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.view.Surface;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class ImageUtils {
    public static Bitmap uriToBitmap(Context context, Uri uri) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            // Return the decoded bitmap from the input stream
            return BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }

    public static String uriToBase64(Context context, Uri uri) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
            return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static Bitmap rotateBitmap(Bitmap bitmap, int rotation) {
        android.graphics.Matrix matrix = new android.graphics.Matrix();
        switch (rotation) {
            case Surface.ROTATION_0:
                matrix.postRotate(90);
                break;
            case Surface.ROTATION_90:
                // No rotation needed
                return bitmap;
            case Surface.ROTATION_180:
                matrix.postRotate(270);
                break;
            case Surface.ROTATION_270:
                matrix.postRotate(180);
                break;
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}

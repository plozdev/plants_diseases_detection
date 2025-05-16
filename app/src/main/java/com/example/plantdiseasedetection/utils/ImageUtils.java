package com.example.plantdiseasedetection.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
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
        if (bitmap == null) {
            return null; // Return null if the bitmap is null.
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try {
            // Compress the bitmap to JPEG format with 90% quality. You can adjust the format and quality as needed.
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);

            // Convert the compressed byte array into a Base64 string.
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace(); // Handle any exceptions during conversion.
            return null;
        } finally {
            try {
                byteArrayOutputStream.close(); // Close the stream after use.
            } catch (Exception ignored) {
            }
        }
    }



}


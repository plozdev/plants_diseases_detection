package com.example.plantdiseasedetection.fragments;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.plantdiseasedetection.MainActivity;
import com.example.plantdiseasedetection.R;
import com.example.plantdiseasedetection.fragments.scan.chup.ScanDataHolder;

public class ResultActivity extends AppCompatActivity {
    private TextView resultTitle, resultDescription;
    private View resultLayout, errorLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        ImageView resultImage = findViewById(R.id.result_image);
        resultTitle = findViewById(R.id.result_title);
        resultDescription = findViewById(R.id.result_description);
        resultLayout = findViewById(R.id.result_layout);
        errorLayout = findViewById(R.id.error_layout);
        Button btnReturnDashboard = findViewById(R.id.btn_return_dashboard);

        btnReturnDashboard.setOnClickListener(v->returnToDashboard());


        //Show photo taken
        Bitmap bitmap = rotateBitmap(ScanDataHolder.getBitmap(), getWindowManager().getDefaultDisplay().getRotation());
        if (bitmap != null)
            resultImage.setImageBitmap(bitmap);


        String result = ScanDataHolder.getResultText();
        if (result != null && !result.equals("Detection Not Found")) {
            showResult(result);
        } else {
            showError();
        }
    }

    private void returnToDashboard() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


    private void showResult(String resultText) {
        resultLayout.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.GONE);
        resultTitle.setText(getString(R.string.detection_result));
        resultDescription.setText(resultText);
    }

    private void showError() {
        resultLayout.setVisibility(View.GONE);
        errorLayout.setVisibility(View.VISIBLE);
    }

    private Bitmap rotateBitmap(Bitmap bitmap, int rotation) {
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
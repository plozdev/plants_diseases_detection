package com.example.plantdiseasedetection.fragments;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.plantdiseasedetection.MainActivity;
import com.example.plantdiseasedetection.R;
import com.example.plantdiseasedetection.databinding.ActivityResultBinding;

public class ResultActivity extends AppCompatActivity {
    private static final String TAG = "ResultActivity";
    private ActivityResultBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        processAnalysisResults();
        binding.btnHome.setOnClickListener(v->startActivity(new Intent(ResultActivity.this, MainActivity.class)));
    }

    private void processAnalysisResults() {

        /**
         * Can't call gemini so simulate result
         */

        // Create scan result object to save to Firestore


        // Update UI with results
        binding.tvDiseaseDetected.setText(getString(R.string.disease));
        binding.tvTreatmentRecommendations.setText(getString(R.string.treatment));
        binding.tvPreventativeMeasures.setText(getString(R.string.preventative));

    }




}
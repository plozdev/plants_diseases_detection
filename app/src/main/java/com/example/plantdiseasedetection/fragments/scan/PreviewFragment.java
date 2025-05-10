package com.example.plantdiseasedetection.fragments.scan;

import static android.content.Context.MODE_PRIVATE;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import com.example.plantdiseasedetection.R;
import com.example.plantdiseasedetection.databinding.FragmentPreviewBinding;
import com.example.plantdiseasedetection.fragments.ResultActivity;
import com.example.plantdiseasedetection.fragments.scan.chup.ScanDataHolder;

public class PreviewFragment extends Fragment {
    private ImageView previewImage;
    private Button btnRetake, btnAnalyze;
    private View loadingOverlay;


    public PreviewFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_preview, container, false);
//        previewImage = view.findViewById(R.id.preview_image);
        FragmentPreviewBinding binding = FragmentPreviewBinding.inflate(inflater,container,false);
        btnRetake = binding.btnRetake;
        previewImage = binding.previewImage;
        btnAnalyze = binding.btnAnalyze;
        loadingOverlay = binding.loadingOverlay;
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bitmap imageBitmap = rotateBitmap(ScanDataHolder.getBitmap(), requireActivity().getWindowManager().getDefaultDisplay().getRotation());
        if (imageBitmap != null)
            previewImage.setImageBitmap(imageBitmap);


        btnRetake.setOnClickListener(v-> {
            ScanDataHolder.clear();
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_scanFragment_to_cameraFragment);
        });

        btnAnalyze.setOnClickListener(v-> {
            //do sth
            analyzeImage();
//            NavHostFragment.findNavController(this).navigate(R.id.action_previewFragment_to_resultFragment);
        });
    }

    private void analyzeImage() {
        btnRetake.setEnabled(false);
        btnAnalyze.setEnabled(false);
        btnAnalyze.setText(getString(R.string.analyze___));
        showLoadingWithAnimation(true);

        // Giả lập xử lý AI (2 giây)
        new Handler().postDelayed(() -> {
            // Gọi API lấy kết quả - giả lập với kết quả cứng
            String resultText = "Disease detected: Leaf Spot\nTreatment: Use XYZ spray";
            boolean isSuccessful = true; // Đổi thành kết quả từ API của bạn

            // Lưu kết quả vào ScanDataHolder
            ScanDataHolder.setResultText(isSuccessful ? resultText : "Detection Not Found");

            // Lưu vào SharedPreferences để hiển thị nhanh trên Home
            requireActivity().getSharedPreferences("recent_activity", MODE_PRIVATE)
                    .edit()
                    .putString("last_result", ScanDataHolder.getResultText())
                    .apply();

            // Chuyển sang ResultActivity
            Intent intent = new Intent(getActivity(), ResultActivity.class);
            startActivity(intent);
            btnRetake.setEnabled(true);
            btnAnalyze.setEnabled(true);
            btnAnalyze.setText(getString(R.string.analyze));
            showLoadingWithAnimation(false);
        }, 2000); // 2 giây

    }
/*
    private void showLoading(boolean b) {
        if (loadingOverlay != null) {
            loadingOverlay.setVisibility(b ? View.VISIBLE : View.GONE);
        }
    }
    */

    private void showLoadingWithAnimation(final boolean show) {
        if (loadingOverlay != null) {
            loadingOverlay.setAlpha(show ? 0f : 1f);
            loadingOverlay.setVisibility(View.VISIBLE);

            loadingOverlay.animate()
                    .alpha(show ? 1f : 0f)
                    .setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (!show) {
                                loadingOverlay.setVisibility(View.GONE);
                            }
                        }
                    });
        }
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        ScanDataHolder.clear();
    }
}
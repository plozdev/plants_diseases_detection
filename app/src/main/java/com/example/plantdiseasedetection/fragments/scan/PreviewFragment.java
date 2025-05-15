package com.example.plantdiseasedetection.fragments.scan;

import static android.content.Context.MODE_PRIVATE;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.plantdiseasedetection.R;
import com.example.plantdiseasedetection.controllers.ScanController;
import com.example.plantdiseasedetection.databinding.FragmentPreviewBinding;
import com.example.plantdiseasedetection.fragments.ResultActivity;
import com.example.plantdiseasedetection.fragments.scan.chup.ScanDataHolder;
import com.example.plantdiseasedetection.utils.ImageUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.List;
import java.util.Locale;

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

        Bitmap imageBitmap = ImageUtils.rotateBitmap(ScanDataHolder.getBitmap(), requireActivity().getWindowManager().getDefaultDisplay().getRotation());
        if (imageBitmap != null)
            previewImage.setImageBitmap(imageBitmap);

        btnRetake.setOnClickListener(v-> {
            ScanDataHolder.clear();
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_scanFragment_to_cameraFragment);
        });
        btnAnalyze.setOnClickListener(v->analyzeImage(imageBitmap));
    }

    private void analyzeImage(Bitmap imageBitmap) {
        btnRetake.setEnabled(false);
        btnAnalyze.setEnabled(false);
        btnAnalyze.setText(getString(R.string.analyze___));
        showLoadingWithAnimation(true);

        String base64Image = ImageUtils.bitmapToBase64(imageBitmap);

        // Đọc thời tiết và khu vực từ SharedPreferences
        String weather = requireActivity()
                .getSharedPreferences("weather_info", MODE_PRIVATE)
                .getString("weather", "Unknown weather");

        getUserRegion(region -> {
            if (region == null) region = "Unknown region";
            sendImageToGemini(base64Image, weather, region);
        });
    }
    // Gửi ảnh và prompt đến Gemini API
    private void sendImageToGemini(String base64Img, String weather, String region) {
        ScanController scanController = new ScanController(requireContext());
        scanController.analyzeImage(base64Img, weather, region, new ScanController.ScanCallback() {
            @Override
            public void onSuccess(String result) {
                ScanDataHolder.setResultText(result);
                Intent intent = new Intent(getActivity(), ResultActivity.class);
                btnRetake.setEnabled(true);
                btnAnalyze.setEnabled(true);
                startActivity(intent);

            }
            @Override
            public void onFailure(String error) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();

                btnAnalyze.setText(getString(R.string.analyze));
                showLoadingWithAnimation(false);
                btnRetake.setEnabled(true);
                btnAnalyze.setEnabled(true);
                Log.e("PreviewFragment", error);
            }
        });
    }

//    @SuppressLint("MissingPermission")
    private void getUserRegion(RegionCallback callback) {
        FusedLocationProviderClient locationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        locationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if (!addresses.isEmpty()) {
                        String region = addresses.get(0).getAdminArea();
                        callback.onRegionDetected(region);
                    } else {
                        callback.onRegionDetected(null);
                    }
                } catch (Exception e) {
                    callback.onRegionDetected(null);
                }
            } else {
                callback.onRegionDetected(null);
            }
        });
    }


    // Callback cho lấy vùng
    interface RegionCallback {
        void onRegionDetected(String region);
    }
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



    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
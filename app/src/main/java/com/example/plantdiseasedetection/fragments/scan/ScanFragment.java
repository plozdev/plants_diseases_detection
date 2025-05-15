package com.example.plantdiseasedetection.fragments.scan;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.plantdiseasedetection.R;
import com.example.plantdiseasedetection.controllers.ScanController;
import com.example.plantdiseasedetection.databinding.FragmentScanBinding;
import com.example.plantdiseasedetection.fragments.scan.chup.ScanDataHolder;
import com.example.plantdiseasedetection.utils.ImageUtils;

public class ScanFragment extends Fragment {
    private FragmentScanBinding binding;
    private ActivityResultLauncher<String> pickImageLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri!=null) {
//                        scanController.processImage(uri); //Xu ly
                        Bitmap bitmap = ImageUtils.uriToBitmap(requireContext(),uri);
                        if (bitmap!=null) {
                            ScanDataHolder.setBitmap(bitmap);
                            NavController navController = Navigation.findNavController(requireView());
                            navController.navigate(R.id.action_scanFragment_to_previewFragment);
                        } else Log.e("ImageUtils", "Failed to convert URI to Bitmap");
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentScanBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnSelectImage.setOnClickListener(v->pickImageLauncher.launch("image/*"));
        NavController navController = Navigation.findNavController(view);

        binding.btnScan.setOnClickListener(v->navController.navigate(R.id.action_scanFragment_to_cameraFragment));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}